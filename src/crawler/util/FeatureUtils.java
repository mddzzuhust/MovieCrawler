package crawler.util;

//import org.zeromq.ZMQ;

/*功能：对下载的图片进行缩放,提取特征并发送特征
 * */

public class FeatureUtils {
//	static {
//		System.loadLibrary("siftExtract"); // 注意：不写扩展名，名字要与dll的文件名一致
//	}

	//public native String getFeature(String imgPath);   //用来提取图片的特征，此功能暂时不用

	/**
	 * @param args
	 */
	public void sendFeature(String imgPath) {
	
		String tempImgPath;  //用于存放压缩过后图片的路径地址
		try{
			tempImgPath = ImageScale.zoomImage(imgPath);
		}catch(Exception e){
			System.out.println("图片缩放失败!"+imgPath);
			return;
		}
                              
//		String feature = getFeature(tempImgPath);  //用于发送特征，此功能暂时不用
//		//System.out.println(feature);
//		if(feature.equals("ERROR"))
//			return;
//		
//		ZMQ.Context context = ZMQ.context(1); // 创建一个I/O线程的上下文
//		ZMQ.Socket socket = context.socket(ZMQ.REQ); // 创建一个request类型的socket，这里可以将其简单的理解为客户端，用于向response端发送数据
//		
//		double r = Math.random();
//		String s = Double.toString(r);
//		byte[] b = s.getBytes();
//		
//		socket.setIdentity(b);
//		
//		socket.connect("tcp://192.168.1.200:5060"); // 与response端建立连接
//
//		//String request = "25-1403174848183.jpg 221 d290f9bb 7b94e9bf 5251d9ba 7bd0b9ab ded0d9bb 58d0b9bb f85ffb16 e992b5ae fc96f70e fc87f39f 7495d5be 9316b52f 3fdecb06 33448eba 32ce0eba c95af16f 1f730e00 29c89cfa 4b8dd8fb 5d1d4a64 6fcb0982 76cc1eba e88aa52e b491cb88 1e48de22 1f434b27 40c9c0a2 fb5ac0ef 12994cf3 ed4bc967 fe9d5aff fd0df5ae ad4ee61b a4d2ca12 9c01f194 ecc880e2 7e574dfb de0d5eb3 5d0de9f7 87991cfb ec52e862 d3945cfb fae9e9e7 fdd6f1fe 379cdef3 1d5e8e00 e506c57f 79caa52c f94aa0aa bd5e4347 44195eea b25b2ad7 8e0d4e93 f9e2e43e f902a53f 9491890a f982a56c eddae062 adc20c84 ef88c4a0 7a8d5afa fa8d5efb eb8aecef a5d6db62 bd3c8b9f 2ce07a22 f992a72c f922a56c 90158b48 ea0d5aff 9497cb40 8a4d4f53 e136bd3f eb57c877 a8daed20 db81e9fb da0d52fe 9051ab70 7d114bdc f964a76c e1b2a52c b04dffda d49dcff4 dae8e9fb cba948b3 ac57c15a 8136a52d 34845a33 2de28082 2824da16 2ce0fa17 b695abde cb8ddcfb d1cdeae3 26d01a50 2976ae1a 68b0852c b01fdad6 d112ebec d056f130 9c59e3b0 f0de6827 62774cb3 9b680cfb 2d35a71a c51c0e86 6d48c0a2 db680cfb fbc84cfb 9110fcdc 6c7f94ea d09c7062 23677cf3 8a88cd7b 1a849d7a 677f14eb 911ca99b 9d4be374 23121662 520faa1c bc9dc2d6 d93e894d 8a616df7 6a6d0dff daf97ab3 4ac8e9ff a5421618 4250e9bb 72b858bb 2e30ca42 a7421618 a0c75f7e f00d5af7 d8dae1ba ef98c4e2 7a0d52fe 5af8d8bb 9851a1db e640da00 efde04a2 f21d5af3 e510844a 751acaea 9851b197 dc9af1ba 326536b2 26402c72 9a58a3b5 9275ed50 e6c0da00 d02d6bd7 f982a5ac e70f05ca 511cdaee e982a1ac d02d6bd7 513d2a84 e640da00 9075e950 511acbcc 185cf135 c71c14c0 43b0b9b9 da71a9f1 27436a18 f8cee07a 2f77ae4b 87d21ad0 ae75ae4b 8fc21ad0 a6e905f8 b84d3fd4 e936a52d 2072ad58 4cc95f5e 3072ad58 4cc95f5e e974252d 98593ad0 eca28262 f36355fe 98dde190 9a593fd1 e8e2602a 786d4ad7 6bea95ee 9a196bd1 ece2e062 b85d3fd4 920d5b92 a8d2a30d fa98a3bd 6cd6e560 905d6394 b9b0c4fa d41d4b95 e4414304 9289dada e8e2e12a 906d7fbe e9c2e72c e882e020 9219fbd0 2675343c 26b0353f 47b116a0 ca0da1f9 a68b65fd 83f5240d 46cf6265 a33ea415 9a80e1e8 445f6df7 ac75a1ed 2800ee0 93e23f11 f73027bd 92695fde e9fae32c d33842b5 2e4a6a41 ";
//		String request = feature;
//		socket.send(request.getBytes(), 0); // 向reponse端发送数据
//		String message = socket.recvStr();
//
//		
//		/**
//		 * message返回结果： 1 FileName:28-1403159478859.jpg,0。802340;28-1403159473893.jpg;28-1403159479715.jpg;28-1403159475180.jpg;28-1403159474273.jpg;  1和top 5
//		 * 				-1 未找到
//		 * 				-2 未知错误
//		 */				
//		System.out.println(" m :" + message);
//		
//		socket.close();
//		context.term();
		
	}
}
