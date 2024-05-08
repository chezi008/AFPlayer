## AFPlayer 安防播放器

基于ijkplayer的播放器

参考与引用：

- ijkplayer:https://github.com/bilibili/ijkplayer
- 录像与截图：https://github.com/zhu528/ijkplayer-record
- 播放器UI:https://github.com/ILoveLin/IjkRecordPlayerAddUI

### 基本功能

| 功能                 | 支持 | 说明         |
| -------------------- | ---- | ------------ |
| rtsp播放，h264流播放 | √    | 部分功能修改 |
| 录像、截图           | √    | 添加功能     |
| 实时kbs统计          | √    | 添加功能     |

### ijkplayer播放实时流

ijkplayer使用起来比较的简单，比如播放rtsp视频的时候只需要定义好最初的网络地址调用`mVideoView.setVideoURI(uri)`就可以进行播放了。网络、视频处理都做了很好的封装，但是这也给我们自定义功能带来了些许的麻烦，有时候我们并不需要播放器的网络部分功能，我们只需要解码的那部分功能。

实现方式：通过IAndroidIO实现

### 问题记录

#### 截屏失败的问题

两种情况截屏会出现闪退：

1、分辨率不匹配的时候

2、使用硬解码的时候