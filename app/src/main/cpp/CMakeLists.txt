cmake_minimum_required(VERSION 3.22.1)
project("bigoguardian_engine")

# 1. Kasih tahu lokasi "Gambar Teknik" (.h)
include_directories(include)

add_library(bigoguardian_engine SHARED native-lib.cpp)

# 2. Daftarkan 7 "Otot" (.so)
foreach(lib avcodec avdevice avfilter avformat avutil swresample swscale)
    add_library(${lib} SHARED IMPORTED)
    set_target_properties(${lib} PROPERTIES IMPORTED_LOCATION 
        ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/lib${lib}.so)
endforeach()

# 3. Rakit semuanya jadi satu
target_link_libraries(bigoguardian_engine 
    android 
    log 
    avutil 
    avcodec 
    avformat 
    swscale 
    swresample 
    avfilter 
    avdevice)
