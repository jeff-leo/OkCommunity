# OkCommunity
## **Ok社区 介绍**
一款以新闻浏览为主，附带各种特色功能的新闻社区，在ok社区，你可以表达对实时新闻的评论，也可以在社区中心表述你的心情，无聊时还可以体验一下各种附带小功能。UI大致还是借鉴了知乎日报的界面风格，但也融入了很多个人的风格。
ok社区的安卓客户端和后台都是我自己写的，虽然安卓开发不需要做后台，但是我觉得最好还是要了解一下后台的知识，这样对整个项目的架构会更加的了解！

### **github地址：[https://github.com/jeff-leo/OkCommunity](https://github.com/jeff-leo/OkCommunity)**

### **app下载链接：[http://www.wandoujia.com/apps/com.liu.sportnews](http://www.wandoujia.com/apps/com.liu.sportnews)**

（欢迎大家star和下载，这是给我的动力）

## **app 演示图：**
- 主界面

![主界面](http://img.blog.csdn.net/20161001154719112)

-  登录注册

![登录注册](http://img.blog.csdn.net/20161001160640388)

- 社区发帖

![社区发帖](http://img.blog.csdn.net/20161001160933267)

- 个人信息

![个人信息](http://img.blog.csdn.net/20161001161100926)

- 更多功能请下载app自行体验。

## **具体模块**
1. 侧边栏是采用DrawerLayout实现，但是我个人不推荐，最好还是使用github的第三方侧边栏，因为DrawerLayout的局限性太大，而第三方功能非常丰富和灵活。

2. 主界面的新闻列表，轮播条的实现思路是，采用handler和message，每隔3s就用handler发送message，通知viewpager让currentPage++；
轮播条下面的列表是采用CardView实现。

```
//利用handler循环发送消息
mHandler = new Handler() {
    public void handleMessage(Message msg) {

         mTopCurrentItem = ((HeaderViewHolder) holder).headerPager.getCurrentItem();

         if (mTopCurrentItem < mTopList.size() - 1) {
                mTopCurrentItem++;
         } else {
                mTopCurrentItem = 0;
         }

         ((HeaderViewHolder) holder).headerPager.setCurrentItem(mTopCurrentItem, false);
         mHandler.sendEmptyMessageDelayed(0, 3000);
    }
};
```

3. 整个app的网络通信框架用的是okHttp，不过要进一步的封装，我使用的是鸿洋的OkHttpUtil。
4. 图片加载使用的Glide，不过Glide会有一点小问题，下面会提到。
5.  新闻详情页是采用5.0的新特性api实现的，[ Android5.x新特性之Toolbar，AppBarLayout，CoordinatorLayout，CollapsingToolbarLayout等汇总](http://blog.csdn.net/jeffleo/article/details/51740624)，不懂得可以看看这个链接。
6.  图片选择器是使用github上的开源框架，[MultiImageSelector](https://github.com/lovetuzitong/MultiImageSelector/blob/master/README_zh.md)。


## **开发途中遇到的问题**
- 主页面的viewpager的嵌套有可能会导致事件冲突，这涉及到事件传递机制和滑动嵌套的问题，这篇文章讲得很好，所以我不再多写，[Android Touch事件传递机制解析](http://blog.csdn.net/jeffleo/article/details/52003346)

- Glide配合圆形image，有可能会导致第一次不能加载，第二次才能加载成功的问题，具体的解决方案：[Glide加载圆形image第一次显示占位图的原因](http://blog.csdn.net/jeffleo/article/details/52097151)

- 主界面的fragment之间的切换，有可能会导致数据不刷新，具体的解决方案：[让多个Fragment 切换时不重新实例化](http://blog.csdn.net/jeffleo/article/details/52174996)，[FragmentPagerAdapter刷新数据原理分析与解决](http://blog.csdn.net/jeffleo/article/details/52008515)

- 在开发登录注册模块时，手机上使用，点击输入框，软键盘弹出时会覆盖掉布局，具体的解决方案：[Android 软键盘遮挡的四种解决方案](http://blog.csdn.net/jeffleo/article/details/52174970)

- 使用ListView时会导致图片移位等问题，并且随着现在开源社区越来越多的开源RecyclerView，所以推荐首选使用RecyclerView。

- 上线的项目一定要在app内开发新版本推送的功能，之前我在网上差的资料十分混乱，后来索性自己写了博客记录了下来，[ Android版本检测更新](http://blog.csdn.net/jeffleo/article/details/52174585)

## **给大家的福利**
在这里，献上我学习安卓以来，收藏的一些非常好的学习资源。

1. 现在的app开发架构，比较流行的是mvp+RxJava+Retrofit，这三种知识点的资料：[给 Android 开发者的 RxJava 详解](http://gank.io/post/560e15be2dca930e00da1083)， [选择恐惧症的福音！教你认清MVC，MVP和MVVM](http://zjutkz.net/2016/04/13/%E9%80%89%E6%8B%A9%E6%81%90%E6%83%A7%E7%97%87%E7%9A%84%E7%A6%8F%E9%9F%B3%EF%BC%81%E6%95%99%E4%BD%A0%E8%AE%A4%E6%B8%85MVC%EF%BC%8CMVP%E5%92%8CMVVM/)， [Android网络请求--Retrofit基础](http://www.jianshu.com/p/70b89e103d5b)
2.  收藏的一些UI图标和设计的网站：[materialpalette](https://www.materialpalette.com/green/indigo)， [UI中国](http://www.ui.cn/)， [阿里巴巴图标库](http://iconfont.cn/)， [iconfinder](https://www.iconfinder.com/)

