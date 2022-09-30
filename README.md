# KeyboardMutual
键盘表情切换完美交互 , demo 效果在 art 文件中。


基本逻辑：
通过设置 SOFT_INPUT_ADJUST_NOTHING，使界面不会跟随键盘而弹起，从而解决切换时闪的问题。
然后用一层布局包裹需要展开的 View，通过改变布局高度来顶起需要被顶起的内容。

基本用法：
1. 初始化
因为 KeyboardMutual 内部需要用到上下文，所以要在 Application 中初始化一下：
```kotlin
class DemoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KeyboardMutual.init(this)
    }
}
```

2. 首先通过 KeyboardLayout 包裹布局：
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <!-- 这里模拟界面内容 -->
    <TextView.../>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <!-- 将输入框等布局封装成一个View方便操作 -->
        <com.lzx.keyboardmutual.ChatInputView ... />

        <!-- 用KeyboardLayout包裹要切换的内容，这里模拟了2个View -->
        <com.lzx.library.KeyboardLayout
            android:id="@+id/keyboardLayout"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:minHeight="300dp">

            <!-- 模拟这里是一个录音界面 -->
            <TextView... />

            <!-- 模拟这里是一个表情界面 -->
            <TextView.../>

        </com.lzx.library.KeyboardLayout>
    </LinearLayout>
</LinearLayout>
```

3. 设置一下界面不跟随键盘而顶起
```kotlin
override fun onStart() {
    super.onStart()
    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
}
```

4. 然后通过 KeyboardLayout 和 keyboardMutual 相关的api去操作展示隐藏，具体用法代码参考 demo 中的 ChatInputView 类。



KeyboardMutual 默认在 Activity onCreate 的时候开始监听键盘，在 onDestroy 时结束监听。

但这不使用于所有场景。有时候需要自己控制时机。

所以 KeyboardMutual 提供了 startObserver 和 stopObserver 方法用于用户自己去操作监听时机。

如果要使用，请在 KeyboardLayout 布局中添加 app:observerKeyboardForMySelf="true" 属性。



代码参考：
https://github.com/ssseasonnn/KeyboardX

https://github.com/liangjingkanji/soft-input-event




