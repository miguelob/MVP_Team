package icai.dtc.isw.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import icai.dtc.isw.controler.UserControler;
import icai.dtc.isw.dao.SurveyDAO;
import icai.dtc.isw.domain.User;
import icai.dtc.isw.message.Message;
import icai.dtc.isw.controler.ProductControler;
import icai.dtc.isw.controler.ReviewControler;
import icai.dtc.isw.domain.Product;
import icai.dtc.isw.domain.Review;

public class SocketServer extends Thread {
	public static final int PORT_NUMBER = 8081;
	protected Socket socket;
	private ProductControler productControler = new ProductControler();
	private UserControler userControler = new UserControler();
	private ReviewControler reviewControler = new ReviewControler();
	private ArrayList<Product> basicProductList;
	private ArrayList<Review> reviewList; 
	private HashMap<String,Object> session = new HashMap<String, Object>();

	private SocketServer(Socket socket) {
		this.socket = socket;
		System.out.println("New client connected from " + socket.getInetAddress().getHostAddress());
		start();
	}

	public void run() {
		User userStatus = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			//first read the object that has been sent
			ObjectInputStream objectInputStream = new ObjectInputStream(in);
		    Message mensajeIn= (Message)objectInputStream.readObject();
		    //Object to return informations 
		    ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
		    Message mensajeOut=new Message();
		    switch (mensajeIn.getContext()) {
		    	case "/getProductBasicInfo":
		    		basicProductList = new ArrayList<Product>();
		    		productControler.getProductBasicInfo(basicProductList);
		    		mensajeOut.setContext("/getBasicProductResponse");
		    		session = new HashMap<String, Object>();
		    		session.put("basicProduct",basicProductList);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;
		    	case "/getProductFullInfo":
		    		Product product = (Product) mensajeIn.getObject();
		    		productControler.getProductFullInfo(product);
		    		mensajeOut.setContext("/getFullProductResponse");
		    		session.put("fullProduct",product);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;
		    	case "/uploadUser":
		    		User user = (User) mensajeIn.getObject();
		    		boolean userUploadStatus = userControler.uploadUser(user);
		    		mensajeOut.setContext("/getUserUploadResponse");
		    		session.put("uploadUser", userUploadStatus);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;
				/*case "/setSessionStatus":
					User userLoggedIn = (User) mensajeIn.getObject();
					session.put("sessionStatus", userLoggedIn);
					mensajeOut.setContext("/setSessionResponse");
					mensajeOut.setSession(session);
					objectOutputStream.writeObject(mensajeOut);

		    	break;
		    	case "/getSessionStatus":
		    		System.out.println(basicProductList);
		    		System.out.println(userStatus.getName());
		    		session.put("sessionStatus", userStatus);
		    		mensajeOut.setContext("/getSessionStatus");
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;*/
		    	case "/uploadReview":
		    		HashMap<String,Object> data = (HashMap<String,Object>) mensajeIn.getObject();
		    		Review review = (Review) data.get("review");
		    		Product productReview = (Product) data.get("product");
		    		HashMap<Integer,int[]> surveyAns = (HashMap<Integer,int[]>) data.get("survey");
		    		boolean surveyUploadStatus = reviewControler.uploadSurvey(productReview,surveyAns);
		    		boolean reviewUploadStatus = reviewControler.uploadReview(review,productReview);
		    		boolean retorno = false;
		    		if(surveyUploadStatus & reviewUploadStatus)
		    			retorno = true;
		    		mensajeOut.setContext("/getReviewUploadResponse");
		    		session.put("uploadReview",retorno);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;
		    	case "/login":
		    		HashMap<String,String> loginData = (HashMap<String,String>) mensajeIn.getObject();
		    		String password = loginData.get("password").toString();
		    		String nameEmail = loginData.get("nameEmail").toString();
		    		User loginUser = userControler.login(nameEmail, password);
		    		mensajeOut.setContext("/getLoginResponse");
		    		session.put("loginUser", loginUser);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;
		    	case "/getUserReviews":
		    		reviewList = new ArrayList<Review>();
		    		User tempUser = (User) mensajeIn.getObject();
		    		reviewControler.getReviews(tempUser, reviewList);
		    		mensajeOut.setContext("/getUserReviewsResponse");
		    		session.put("userReviews",reviewList);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;
		    	case "/refreshProductScore":
		    		Product producto = (Product) mensajeIn.getObject();
		    		int score = productControler.refreshScore(producto);
		    		mensajeOut.setContext("/getRefreshScoreResponse");
		    		session.put("refreshScore", score);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    				
		    	break;
		    	case "/setFavoriteStatus":
		    		HashMap<String,Object> favData = (HashMap<String,Object>) mensajeIn.getObject();
		    		Product favProduct = (Product) favData.get("product");
		    		boolean favStatus = (boolean) favData.get("value");
		    		User favUser = (User) favData.get("user");
		    		boolean status = userControler.setFavorite(favProduct,favUser,favStatus);
		    		mensajeOut.setContext("/setFavoriteStatusResponse");
		    		session.put("favStatus", status);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;
		    	case "/getFavoriteStatus":
		    		HashMap<String,Object> dataTemp = (HashMap<String,Object>) mensajeIn.getObject();
		    		Product productQue = (Product) dataTemp.get("product");
		    		User userQue = (User) dataTemp.get("user");
		    		boolean retornoTemp = userControler.getFavoriteStatus(productQue,userQue);
		    		mensajeOut.setContext("/getFavoriteStatusResponse");
		    		session.put("getFavStatus", retornoTemp);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;
		    	case "/getFavoriteProducts":
		    		User queryUser = (User) mensajeIn.getObject();
		    		ArrayList<Product> favorites = new ArrayList<Product>();
		    		userControler.getFavorites(queryUser, favorites);
		    		mensajeOut.setContext("/getFavoritesResponse");
		    		session.put("getFavorites", favorites);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;
		    	/*case "/getUser":
		    		userList = new ArrayList<User>();
		    		//userControler.getUser(userList);
		    		mensajeOut.setContext("/getUser");
		    		session.put("users",userList);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;
		    	case "/getReview":
		    		reviewList = new ArrayList<Review>();
		    		reviewControler.getReview(reviewList);
		    		mensajeOut.setContext("/getReview");
		    		session.put("reviews",reviewList);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);
		    	break;*/
		    		
		    	
		    	
		    	default:
		    		System.out.println("\nParametro no encontrado");
		    		break;
		    		
		    		/*case "/getCustomer":
		    		CustomerControler customerControler=new CustomerControler();
		    		ArrayList<Customer> lista=new ArrayList<Customer>();
		    		customerControler.getCustomer(lista);
		    		mensajeOut.setContext("/getCustomerResponse");
		    		HashMap<String,Object> session=new HashMap<String, Object>();
		    		session.put("Customer",lista);
		    		mensajeOut.setSession(session);
		    		objectOutputStream.writeObject(mensajeOut);		    		
		    	break;
		    	case "/getTest":
		    		TestControler testControler=new TestControler();
		    		ArrayList<Test> lista1=new ArrayList<Test>();
		    		testControler.getTests(lista1);
		    		mensajeOut.setContext("/getTestResponse");
		    		HashMap<String,Object> session1=new HashMap<String, Object>();
		    		session1.put("Test",lista1);
		    		mensajeOut.setSession(session1);
		    		objectOutputStream.writeObject(mensajeOut);		    		
		    	break;*/
		    }
		    
		    //Lógica del controlador 
		    //System.out.println("\n1.- He leído: "+mensaje.getContext());
		    //System.out.println("\n2.- He leído: "+(String)mensaje.getSession().get("Nombre"));
		    
		    
		    
		    //Prueba para esperar
		    /*try {
		    	System.out.println("Me duermo");
				Thread.sleep(15000);
				System.out.println("Me levanto");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			// create an object output stream from the output stream so we can send an object through it
			/*ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
			
			//Create the object to send
			String cadena=((String)mensaje.getSession().get("Nombre"));
			cadena+=" añado información";
			mensaje.getSession().put("Nombre", cadena);
			//System.out.println("\n3.- He leído: "+(String)mensaje.getSession().get("Nombre"));
			objectOutputStream.writeObject(mensaje);*
			*/
		    
		    //test prueba
		    
		} catch (IOException ex) {
			System.out.println("Unable to get streams from client");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("Connected to Kosmetics_Server");
		ServerSocket server = null;
		try {
			server = new ServerSocket(PORT_NUMBER);
			while (true) {
				/**
				 * create a new {@link SocketServer} object for each connection
				 * this will allow multiple client connections
				 */
				new SocketServer(server.accept());
			}
		} catch (IOException ex) {
			System.out.println("Unable to start server.");
		} finally {
			try {
				if (server != null)
					server.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}