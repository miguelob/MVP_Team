package icai.dtc.isw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import icai.dtc.isw.domain.Review;

public class ReviewDAO {
	public static void getReview(ArrayList<Review> lista) {
		Connection con=ConnectionDAO.getInstance().getConnection();
		try (PreparedStatement pst = con.prepareStatement("SELECT * FROM \"Review\"");
                ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
            	lista.add(new Review(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getDouble(5), rs.getString(7)));
            }

        } catch (SQLException ex) {

            System.out.println(ex.getMessage());
        }
	}
	
	public static void main(String[] args) {
		
		
		ArrayList<Review> lista=new ArrayList<Review>();
		ReviewDAO.getReview(lista);
		
		
		 for (Review review : lista) {			
			System.out.println(review);
	}
}
