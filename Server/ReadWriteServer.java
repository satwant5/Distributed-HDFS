import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



public class ReadWriteServer extends UnicastRemoteObject implements ReadWriteInterface{

	
	protected ReadWriteServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static int CHUNK_SIZE = 64*1024;
	
	public static String CHUNK = "chunk";
	
//	public static String outfolder = "serverout";
	
	public static String registryname = "ReadWriteServer";
	
	@Override
	public int FileWrite64K(String filename, long offset, byte[] data)
			throws IOException, RemoteException {
	/*	File outf = new File(outfolder);
		if(!outf.exists()) {
			System.out.println("outfile "+outf.getAbsolutePath());
			outf.mkdir();
		}*/
		File f = new File(filename);
		if(!f.exists()) {
			f.mkdir();
		}
		//System.out.println("offset "+offset);
		long chunkno = offset/CHUNK_SIZE+1;
		//System.out.println("LENGTH OF DATA "+data.length);
		if(data.length == CHUNK_SIZE) {
			File chunkfile = new File(f, CHUNK+chunkno);
	//		System.out.println("Chunk file"+chunkfile.getAbsolutePath());
			chunkfile.createNewFile();
			FileOutputStream fos = new FileOutputStream(chunkfile);
			fos.write(data);
			fos.close();
			return 0;
		} else {
			return -1;
		}
	}

	@Override
	public long NumFileChunks(String filename) throws IOException,
			RemoteException {
		File dir = new File(filename);
		long size = dir.getTotalSpace();
		long noofchunks = size/CHUNK_SIZE;
		return noofchunks;
	}

	@Override
	public byte[] FileRead64K(String filename, long offset) throws IOException,
			RemoteException {
		File dir = new File(filename);
		long chunkno = offset/CHUNK_SIZE+1;
		File chunkfile = new File(dir,CHUNK+chunkno);
		//System.out.println("Reading chunk file "+chunkfile.getAbsolutePath());
		if(!chunkfile.exists()) {
			return null;
		}
		FileInputStream fis = new FileInputStream(chunkfile);
		byte [] data = new byte[CHUNK_SIZE];
		fis.read(data);
		
		return data;
	}

	public static void main(String args[]) {
		try {
		int portno = Integer.parseInt(args[0]);
		//int portno = 3234;
		//String hostname = (InetAddress.getLocalHost()).toString();
	//	System.out.println("Hostname "+hostname);
		ReadWriteServer obj = new ReadWriteServer();
		// Bind the remote object's stub in the registry
		Registry registry = LocateRegistry.createRegistry(portno);
		registry.rebind(registryname, obj);
		//System.err.println("Server ready");
		} catch (Exception e) {
		//System.err.println("Server exception: " + e.toString());
		e.printStackTrace();
		}
		}
	
}
