#include <jni.h>
#include <string>
#include <unistd.h>
#include <atomic>
#include <map>
#include <mutex>

extern "C" {
#include <libavformat/avformat.h>
#include <libavutil/timestamp.h>
#include <libavutil/mathematics.h>
}

std::map<int, std::atomic<bool>*> stop_flags;
std::map<int, std::atomic<long>*> live_durations;
std::mutex engine_mutex;

extern "C" JNIEXPORT jint JNICALL
Java_com_example_bigoguardian_RecorderService_startNativeRecording(JNIEnv *env, jobject thiz, jint id, jstring jurl, jstring jpath) {
    const char *url = env->GetStringUTFChars(jurl, 0);
    const char *path = env->GetStringUTFChars(jpath, 0);
    
    std::atomic<bool> *stop_ptr = new std::atomic<bool>(false);
    std::atomic<long> *dur_ptr = new std::atomic<long>(0);
    
    {
        std::lock_guard<std::mutex> lock(engine_mutex);
        stop_flags[id] = stop_ptr;
        live_durations[id] = dur_ptr;
    }

    AVFormatContext *ifmt_ctx = NULL, *ofmt_ctx = NULL;
    AVPacket pkt;
    int ret, i;
    int64_t first_pts = -1;

    if ((ret = avformat_open_input(&ifmt_ctx, url, NULL, NULL)) < 0) return ret;
    if ((ret = avformat_find_stream_info(ifmt_ctx, NULL)) < 0) return ret;

    avformat_alloc_output_context2(&ofmt_ctx, NULL, NULL, path);
    if (!ofmt_ctx) return -1;

    for (i = 0; i < ifmt_ctx->nb_streams; i++) {
        AVStream *out_stream = avformat_new_stream(ofmt_ctx, NULL);
        avcodec_parameters_copy(out_stream->codecpar, ifmt_ctx->streams[i]->codecpar);
        out_stream->codecpar->codec_tag = 0;
    }

    if (!(ofmt_ctx->oformat->flags & AVFMT_NOFILE)) avio_open(&ofmt_ctx->pb, path, AVIO_FLAG_WRITE);
    avformat_write_header(ofmt_ctx, NULL);

    while (!stop_ptr->load()) {
        ret = av_read_frame(ifmt_ctx, &pkt);
        if (ret < 0) break;
        
        AVStream *in_stream = ifmt_ctx->streams[pkt.stream_index];
        AVStream *out_stream = ofmt_ctx->streams[pkt.stream_index];

        if (pkt.pts != AV_NOPTS_VALUE) {
            if (first_pts == -1) first_pts = pkt.pts;
            dur_ptr->store((long)((pkt.pts - first_pts) * av_q2d(in_stream->time_base)));
        }

        pkt.pts = av_rescale_q(pkt.pts, in_stream->time_base, out_stream->time_base);
        pkt.dts = av_rescale_q(pkt.dts, in_stream->time_base, out_stream->time_base);
        pkt.duration = av_rescale_q(pkt.duration, in_stream->time_base, out_stream->time_base);
        pkt.pos = -1;

        av_interleaved_write_frame(ofmt_ctx, &pkt);
        av_packet_unref(&pkt);
    }

    av_write_trailer(ofmt_ctx);
    avformat_close_input(&ifmt_ctx);
    if (ofmt_ctx && !(ofmt_ctx->oformat->flags & AVFMT_NOFILE)) avio_closep(&ofmt_ctx->pb);
    avformat_free_context(ofmt_ctx);
    
    env->ReleaseStringUTFChars(jurl, url);
    env->ReleaseStringUTFChars(jpath, path);
    return 0;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_bigoguardian_RecorderService_getNativeDuration(JNIEnv *env, jobject thiz, jint id) {
    std::lock_guard<std::mutex> lock(engine_mutex);
    if (live_durations.count(id)) return live_durations[id]->load();
    return 0;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_bigoguardian_RecorderService_stopNativeRecording(JNIEnv *env, jobject thiz, jint id) {
    std::lock_guard<std::mutex> lock(engine_mutex);
    if (stop_flags.count(id)) stop_flags[id]->store(true);
}
