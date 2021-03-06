import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ReadWriteClient {

	public static int CHUNK_SIZE = 64 * 1024;
	
	public static String registryname = "ReadWriteServer";
	static ReadWriteInterface rwinterface = null;

	public static void main(String[] args) {
		String filename = args[0];
		//String outfilename = args[1];
	//	String filename = "input2.txt";
		//String outfilename = "out2.txt";
		String serverhost = args[1];
		int portno = Integer.parseInt(args[2]);
		File f = new File("output");
		if(!f.exists()) {
			f.mkdir();
		}
		try {
			Registry reg = LocateRegistry.getRegistry(serverhost, portno);
		Remote remobj =	reg.lookup(registryname);
		 rwinterface = (ReadWriteInterface)remobj;
		writeFiletoServer(filename);
		readFilefromServer(filename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void writeFiletoServer(String filename) {
		File f = new File(filename);
		/* try {
			FileOutputStream fos = new FileOutputStream(f);
			byte [] b = new byte[CHUNK_SIZE];
			for(int i=0;i<CHUNK_SIZE;i++) {
				b[i] = (byte)i;
			}
			//System.out.println("B "+b.length);
			fos.write(b);
			for(int i=0;i<CHUNK_SIZE;i++) {
				b[i] = (byte)(i+1);
			}
			fos.write(b);
			for(int i=0;i<CHUNK_SIZE;i++) {
				b[i] = (byte)(i+2);
			}
			fos.write(b);
			for(int i=0;i<CHUNK_SIZE;i++) {
				b[i] = (byte)(i+3);
			}
			fos.write(b);
			//fos.write(b,0,600);
			fos.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} */
		try {
			FileInputStream fis = new FileInputStream(f);
			byte[] data = new byte[CHUNK_SIZE];
			int offset = 0;
			int readbytes = 0;
			while ((readbytes = fis.read(data)) != -1) {
			//	System.out.println("Data readed "+data.length);
				byte [] datatobesend = new byte[readbytes];
				for(int i=0;i<readbytes;i++) {
					datatobesend[i] = data[i];
				}
				int ret = rwinterface.FileWrite64K(filename, offset, datatobesend);
				if(ret == -1) {
					break;
				}
				offset = offset+CHUNK_SIZE;
				data = new byte[CHUNK_SIZE];
			//	System.out.println("Chunk file"+chunkfile.getAbsolutePath());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void readFilefromServer(String filename) {
		try {
			int offset = 0;
			String outfilename = "output"+"/"+filename;
			File outfile = new File(outfilename);
			outfile.createNewFile();
			FileOutputStream fos = new FileOutputStream(outfile, true);
			byte[] data = rwinterface.FileRead64K(filename, offset);		
			while (data != null) {
				fos.write(data);
				offset = offset + CHUNK_SIZE;
				data = rwinterface.FileRead64K(filename, offset);
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}