# Sorry-Android
![img](https://github.com/10000RunningAlpaca/Sorry-Android/blob/master/readmefiles/ceshi.gif)   
生成为所欲为动图的安卓实现   
灵感和视频模板资源来自 https://github.com/xtyxtyx/sorry     
全是Kotlin的，我不想写Java代码了   
转换原理：使用ffmpeg将mp4文件转换为gif，再逐帧抽取bitmap合成为带文字的，重新生成为gif   
mp4 -ffmpeg-> 无字gif -GifDrawalbe-> 带字Bitmap -AnimationGifEncoder-> 带字幕gif文件   
   
ffmpeg: https://github.com/madhavanmalolan/ffmpegandroidlibrary   
GifDrawable: https://github.com/koral--/android-gif-drawable   
AnimationGifEncoder: https://github.com/nbadal/android-gif-encoder   
   
打包好的apk可以直接下载安装   
   
![img](https://github.com/10000RunningAlpaca/Sorry-Android/blob/master/readmefiles/example.gif)   
   
如果要加入自己的模板，在assets/templates目录下放mp4和json文件，json文件内为列表显示名称和台词时间轴 
   
TODO   

* 修改文字效果、位置   
* 修改输出路径   
* 自定义模板   
