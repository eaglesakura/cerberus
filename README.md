# RxAndroid Support

[![Circle CI](https://circleci.com/gh/eaglesakura/rxandroid-support.png?style=badge)](https://circleci.com/gh/eaglesakura/rxandroid-support)

[JavaDoc](http://eaglesakura.github.io/maven/doc/rxandroid-support/javadoc/)

## 概要

RxAndroidでActivity/Fragment等を扱う際に必要になるライフサイクル系処理を簡単に記述するためのサポートライブラリです。

retro-lambdaを使用し、ラムダ式を用いることでコードの可読性の向上が行えます。

## LICENSE

プロジェクトの都合に応じて、下記のどちらかを選択してください。

* アプリ等の成果物で権利情報を表示可能な場合
	* 権利情報の表示を行う（行える）場合、MIT Licenseを使用してください。
	* [MIT License](LICENSE-MIT.txt)
* 何らかの理由で権利情報を表示不可能な場合
	* 何らかの事情によりライセンス表記を行えない場合、下記のライセンスで使用可能です。
	* ライブラリ内で依存している別なライブラリについては、必ずそのライブラリのライセンスに従ってください。
	* [NYSL(English)](LICENSE-NYSL-eng.txt)
	* [NYSL(日本語)](LICENSE-NYSL-jpn.txt)

## 使用例

### build.gradle

 1. repositoriesブロックにリポジトリURLを追加する
 1. dependenciesブロックに任意バージョンのライブラリを追加する
 	* 現時点では1.0.+系列

<pre>
repositories {
    maven { url "http://eaglesakura.github.io/maven/" }		// add maven repo
    mavenCentral()
}

dependencies {
    compile 'com.eaglesakura:rxandroid-support:1.0.+'	// add library
}


</pre>
