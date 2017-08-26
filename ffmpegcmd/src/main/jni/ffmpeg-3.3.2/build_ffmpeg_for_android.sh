#!/bin/bash

echo "###### 开始编译 ffmpeg ######"

SOURCE=$TARGET_FFMPEG_DIR
echo $SOURCE
cd $SOURCE


ADD_H264_FEATURE="--enable-encoder=aac \
    --enable-decoder=aac \
    --enable-gpl \
    --enable-encoder=libx264 \
    --enable-libx264 \
    --extra-cflags=-I$PREFIX/include \
    --extra-ldflags=-L$PREFIX/lib "

function build_one_so
{
    ./configure \
        --prefix=$PREFIX \
        --disable-shared \
        --enable-pthreads \
        --enable-gpl \
        --enable-version3 \
        --enable-nonfree \
        --enable-static \
        --enable-ffmpeg \
        --disable-ffplay \
        --disable-ffprobe \
        --disable-ffserver \
        --disable-doc \
        --disable-symver \
        --enable-avdevice \
        --enable-avfilter \
        --enable-libx264 \
        --enable-small \
        --enable-asm \
        --enable-neon \
        --cross-prefix=$CROSS_PREFIX \
        --target-os=linux \
        --arch=arm \
        --enable-cross-compile \
        --enable-runtime-cpudetect \
        --sysroot=$SYSROOT \
        --disable-parsers \
        --enable-parser=aac \
        --enable-parser=h264 \
        --enable-parser=mjpeg \
        --enable-parser=png \
        --enable-parser=bmp \
        --enable-parser=mpegvideo \
        --enable-parser=mpegaudio \
        --disable-encoders \
        --enable-encoder=h263 \
        --enable-encoder=libx264 \
        --enable-encoder=mpeg4 \
        --enable-encoder=aac \
        --enable-encoder=png \
        --enable-encoder=gif \
        --enable-encoder=bmp \
        --disable-decoders \
        --enable-decoder=h263 \
        --enable-decoder=h264 \
        --enable-decoder=mpeg4 \
        --enable-decoder=mjpeg \
        --enable-decoder=gif \
        --enable-decoder=mp3 \
        --enable-decoder=aac \
        --enable-decoder=png \
        --enable-decoder=bmp \
        --enable-decoder=yuv4 \
        --disable-network \
        --enable-protocols \
        --enable-protocol=concat \
        --enable-protocol=hls \
        --enable-protocol=file \
        --disable-demuxers \
        --enable-demuxer=h263 \
        --enable-demuxer=h264 \
        --enable-demuxer=flv \
        --enable-demuxer=gif \
        --enable-demuxer=aac \
        --enable-demuxer=ogg \
        --enable-demuxer=dts \
        --enable-demuxer=mp3 \
        --enable-demuxer=mov \
        --enable-demuxer=m4v \
        --enable-demuxer=concat \
        --enable-demuxer=mpegts \
        --enable-demuxer=mjpeg \
        --enable-demuxer=mpegvideo \
        --enable-demuxer=rawvideo \
        --enable-demuxer=yuv4mpegpipe \
        --disable-muxers \
        --enable-muxer=h264 \
        --enable-muxer=flv \
        --enable-muxer=gif \
        --enable-muxer=mp3 \
        --enable-muxer=dts \
        --enable-muxer=mp4 \
        --enable-muxer=mov \
        --enable-muxer=mpegts \
        --disable-filters \
        --enable-filter=amix \
        --enable-filter=aresample \
        --enable-filter=asetpts \
        --enable-filter=setpts \
        --enable-filter=ass \
        --enable-filter=scale \
        --enable-filter=crop \
        --enable-filter=concat \
        --enable-filter=atempo \
        --enable-filter=movie \
        --enable-filter=overlay \
        --enable-filter=rotate \
        --enable-filter=select \
        --enable-filter=volume \
        --enable-filter=transpose \
        --enable-filter=hflip \
        --extra-cflags="-mfloat-abi=softfp -mfpu=neon -marm -march=armv7-a" \
        $ADD_H264_FEATURE

    make clean
    make -j4
    make install
}

build_one_so

echo "###### ffmpeg编译完成 ######"
cd ../

