JBossのNettyでつくるサーバーとクライアントの動作確認用プログラム

textベースでデータをやりとりする単純なサーバー
com.ttProject.nettyTest.text.TextServer

textベースでデータをおくるだけの単純なクライアント
com.ttProject.nettyTest.text.TextClient

binaryベースでデータをやり取りする単純なサーバー
com.ttProject.nettyTest.binary.BinaryServer

binaryベースでデータをやり取りする単純なクライアント
com.ttProject.nettyTest.binary.BinaryClient

バイナリをあとで作りましたが、こちらの方が単純ですね＾＾；

必要なnetty
　netty-3.1.5.GA.jar(flazrのデータから取得)
あとUtils.toHexをつかうために、flazrのソースコードのプロジェクトを参照しています。