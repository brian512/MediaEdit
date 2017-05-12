#!/bin/bash

#配置NDK路径
export NDK=/opt/Sdk/ndk-bundle

# 检测NDK
if [[  -z "$NDK"  ]]; then
    echo "The NDK dir is empty, If the shell can not run normally, you should set the NDK variable to your local ndk.dir"
    exit 1
fi

# 检测系统
OS=`uname`
HOST_ARCH=`uname -m`
export CCACHE=; type ccache >/dev/null 2>&1 && export CCACHE=ccache
if [ $OS == 'Linux' ]; then
    export HOST_SYSTEM=linux-$HOST_ARCH
elif [ $OS == 'Darwin' ]; then
    export HOST_SYSTEM=darwin-$HOST_ARCH
fi

# 配置 SYSROOT 和 CROSS_PREFIX，基于android-15
SYSROOT=$NDK/platforms/android-15/arch-arm
CROSS_PREFIX=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/$HOST_SYSTEM/bin/arm-linux-androideabi-
echo "CROSS_PREFIX=$CROSS_PREFIX"


ROOT_DIR=`pwd`/
COMMOND_DIR=command_source
echo $ROOT_DIR

#配置输出路径
PREFIX=$ROOT_DIR/build
if [[ ! -d "$PREFIX" ]]; then
    mkdir $PREFIX
fi

#### 编译步骤 开始 ####
#1、编译x264
#2、编译ffmpeg
#3、ndk-build
#### 编译步骤 结束 ####


#预先编译x264，编译类型为静态库#
X264_SOURCE_DIR=x264
if [[ ! -d "$X264_SOURCE_DIR" ]]; then
    git clone http://git.videolan.org/git/x264.git $X264_SOURCE_DIR
fi
cp $ROOT_DIR$COMMOND_DIR/build_x264_for_android.sh $X264_SOURCE_DIR/build_x264_for_android.sh
TARGET_X264_DIR=$ROOT_DIR$X264_SOURCE_DIR
source $X264_SOURCE_DIR/build_x264_for_android.sh


#编译ffmpeg，编译为多个静态库#
FFMPEG_SOURCE_DIR=ffmpeg-3.3
if [[ ! -d "$FFMPEG_SOURCE_DIR" ]]; then
    git clone git://source.ffmpeg.org/ffmpeg.git $FFMPEG_SOURCE_DIR
fi
cp $ROOT_DIR$COMMOND_DIR/build_ffmpeg_for_android.sh $FFMPEG_SOURCE_DIR/build_ffmpeg_for_android.sh
TARGET_FFMPEG_DIR=$ROOT_DIR$FFMPEG_SOURCE_DIR
source $FFMPEG_SOURCE_DIR/build_ffmpeg_for_android.sh


#执行ndk-build
$NDK/ndk-build
