package crawler.util;

import java.io.File;

public class Test {

	public static void main(String[] args) {
		File file = FileFactory.createFile(FileFactory.Type.Audio, "avi");
		System.out.println(file.getName());
//		for(int i = 0; i < 20; i++)
//			new Thread(new Tester(i+"")).start();
	}
	
}

class Tester implements Runnable {
	
	public static java.util.Set<String> set = java.util.Collections
			.synchronizedSet(new java.util.HashSet<String>());

	public String name;
	
	public Tester(String name) {
		this.name = name;
	}
	
	String[] type = { 
			FileFactory.Type.Video, 
			FileFactory.Type.Audio,
			FileFactory.Type.Image, 
			FileFactory.Type.Page,
			FileFactory.Type.Unknown };
	String[] format = { "jpg", "bmp", "jpeg", "gif" };
	
	@Override
	public void run() {
		java.util.Random rand = new java.util.Random();
		File file = null;
		// TODO Auto-generated method stub
		/*for (int i = 0; i < 50; i++) {
			file = FileFactory.createFile(
					type[rand.nextInt(type.length)], 
					format[rand.nextInt(format.length)]);
			String path = file.getAbsolutePath();
			System.out.println(name + " get file:" + path);
			synchronized(set) {
				if(set.contains(path))
					System.err.println("error: duplicate file:" + path);
				else
					set.add(path);
			}
		}*/
		for (int i = 0; i < 50; i++) {
			int r = rand.nextInt(3);
			int k = rand.nextInt(format.length);
			if(r == 0)
				file = FileFactory.createVideoFile(format[k]);
			else if(r == 1)
				file = FileFactory.createAudioFile(format[k]);
			else if(r == 2)
				file = FileFactory.createImageFile(format[k]);
			
			String path = file.getAbsolutePath();
			System.out.println(name + " get file:" + path);
			synchronized(set) {
				if(set.contains(path))
					System.err.println("error: duplicate file:" + path);
				else
					set.add(path);
			}
		}
	}
	
}