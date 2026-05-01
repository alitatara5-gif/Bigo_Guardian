cmake_minimum_required(VERSION 3.22.1)
project("bigoguardian")

# Arahkan ke folder jniLibs yang baru kita buat
set(libs_DIR ${CMAKE_SOURCE_DIR}/../jniLibs/arm64-v8a)

foreach(lib avcodec avdevice avfilter avformat avutil swresample swscale)
    add_library(${lib} SHARED IMPORTED)
    set_target_properties(${lib} PROPERTIES IMPORTED_LOCATION ${libs_DIR}/lib${lib}.so)
endforeach()

add_library(bigoguardian_engine SHARED native-lib.cpp)
include_directories(include)

target_link_libraries(bigoguardian_engine
    avcodec avformat avutil swresample swscale avfilter avdevice
    android log z)
