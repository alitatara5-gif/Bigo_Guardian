cmake_minimum_required(VERSION 3.22.1)
project("bigoguardian")

set(ffmpeg_DIR ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})

foreach(lib avcodec avdevice avfilter avformat avutil swresample swscale)
    add_library(${lib} SHARED IMPORTED)
    set_target_properties(${lib} PROPERTIES IMPORTED_LOCATION ${ffmpeg_DIR}/lib${lib}.so)
endforeach()

add_library(bigoguardian_engine SHARED native-lib.cpp)

# Cukup arahkan ke folder include utama
include_directories(include)

target_link_libraries(bigoguardian_engine
    avcodec avformat avutil swresample swscale avfilter avdevice
    android log z)
