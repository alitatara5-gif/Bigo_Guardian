#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
}

#define LOG_TAG "BIGO_NATIVE"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jint JNICALL
Java_com_example_bigoguardian_Recorder_executeRemux(JNIEnv *env, jobject thiz, jstring input_url, jstring output_path) {
    const char *in_url = env->GetStringUTFChars(input_url, 0);
    const char *out_path = env->GetStringUTFChars(output_path, 0);

    AVFormatContext *ifmt_ctx = nullptr, *ofmt_ctx = nullptr;
    AVPacket pkt;
    int ret;

    LOGI("[START] Menghubungkan ke: %s", in_url);

    if ((ret = avformat_open_input(&ifmt_ctx, in_url, nullptr, nullptr)) < 0) {
        LOGE("[ERROR] Gagal buka input: %d", ret);
        return ret;
    }

    avformat_alloc_output_context2(&ofmt_ctx, nullptr, nullptr, out_path);
    if (!ofmt_ctx) return -1;

    for (int i = 0; i < ifmt_ctx->nb_streams; i++) {
        AVStream *in_stream = ifmt_ctx->streams[i];
        AVStream *out_stream = avformat_new_stream(ofmt_ctx, nullptr);
        avcodec_parameters_copy(out_stream->codecpar, in_stream->codecpar);
        out_stream->codecpar->codec_tag = 0;
    }

    if (!(ofmt_ctx->oformat->flags & AVFMT_NOFILE)) {
        ret = avio_open(&ofmt_ctx->pb, out_path, AVIO_FLAG_WRITE);
        if (ret < 0) return ret;
    }

    avformat_write_header(ofmt_ctx, nullptr);

    while (av_read_frame(ifmt_ctx, &pkt) >= 0) {
        AVStream *in_stream  = ifmt_ctx->streams[pkt.stream_index];
        AVStream *out_stream = ofmt_ctx->streams[pkt.stream_index];
        pkt.pts = av_rescale_q_rnd(pkt.pts, in_stream->time_base, out_stream->time_base, (AVRounding)(AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX));
        pkt.dts = av_rescale_q_rnd(pkt.dts, in_stream->time_base, out_stream->time_base, (AVRounding)(AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX));
        pkt.duration = av_rescale_q(pkt.duration, in_stream->time_base, out_stream->time_base);
        pkt.pos = -1;
        av_interleaved_write_frame(ofmt_ctx, &pkt);
        av_packet_unref(&pkt);
    }

    av_write_trailer(ofmt_ctx);
    avformat_close_input(&ifmt_ctx);
    if (ofmt_ctx && !(ofmt_ctx->oformat->flags & AVFMT_NOFILE)) avio_closep(&ofmt_ctx->pb);
    avformat_free_context(ofmt_ctx);

    env->ReleaseStringUTFChars(input_url, in_url);
    env->ReleaseStringUTFChars(output_path, out_path);
    LOGI("[DONE] Rekaman Selesai!");
    return 0;
}
