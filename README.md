# Sorry-Android
![img](https://github.com/10000RunningAlpaca/Sorry-Android/blob/master/readmefiles/ceshi.gif)    
生成为所欲为动图的安卓实现    
灵感和视频模板资源来自 https://github.com/xtyxtyx/sorry    
大部分是Kotlin的，一部分别的库搞来的是java    
    
转换原理：使用MediaCodec解码，将帧转为bitmap后画上文字，写入gif    
    
MediaCodec示例: https://bigflake.com/mediacodec/
GifDrawable: https://github.com/koral--/android-gif-drawable    
AnimationGifEncoder: https://github.com/nbadal/android-gif-encoder    
      
   
![img](https://github.com/10000RunningAlpaca/Sorry-Android/blob/master/readmefiles/example.gif)    
    
如果要加入自己的模板，在assets/templates目录下放mp4和json文件，json文件内为列表显示名称和台词时间轴    
    
TODO   
* 修改文字效果、位置   
* 修改输出路径   
* 自定义模板   
