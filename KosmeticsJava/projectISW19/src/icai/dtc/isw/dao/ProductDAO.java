package icai.dtc.isw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import icai.dtc.isw.domain.Product;


public class ProductDAO {
	
	
	
	public static void getProductBasicInfo(ArrayList<Product> lista) {
		Connection con=ConnectionDAO.getInstance().getConnection();
		try (PreparedStatement pst = con.prepareStatement("SELECT * FROM \"Products\"");
                ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
            	//this will change
            	lista.add(new Product(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getDouble(5), rs.getString(6),rs.getBytes(8)));
            }

        } catch (SQLException ex) {

            System.out.println(ex.getMessage());
        }
		for(int i = 0 ; i<lista.size();i++) {
			try (PreparedStatement pst = con.prepareStatement("SELECT AVG(\"Score_Product\") FROM public.\"Reviews\" WHERE \"ID_Product\" = " + lista.get(i).getId());
				 ResultSet rs = pst.executeQuery()) {
					if(rs.next()) {
						lista.get(i).setScore(rs.getFloat(1));
					}
			} catch (SQLException ex) {
	
	            System.out.println(ex.getMessage());
	        }
			//FIRST QUERY OF CHARACTERISTICS
			try (PreparedStatement pst2 = con.prepareStatement("SELECT \"Characteristic\" FROM public.\"Products\" as A1 inner join \"IDs_Prod_Charac\" as B1 on A1.\"ID_Product\" = B1.\"ID_Product\" inner join \"Characteristics\" AS c1 on B1.\"ID_Characteristic\" = C1.\"ID_Characteristic\" WHERE A1.\"ID_Product\" =" + lista.get(i).getId());
					ResultSet rs2 = pst2.executeQuery()) {
	
	            while (rs2.next()) {
	            	lista.get(i).addFeature(rs2.getString(1));
	            }
	
	        } catch (SQLException ex) {
	
	            System.out.println(ex.getMessage());
	        }
		}
		
	}
	public static void getProductFullInfo(Product product) {
		Connection con=ConnectionDAO.getInstance().getConnection();
		ReviewDAO.loadProductReview(product);
		//QUERY PARA SACAR EL ID DE ENCUESTA ASOCIADA AL PRODUCTO
		try (PreparedStatement pst = con.prepareStatement("SELECT \"ID_Survey\" FROM  \"Products\" WHERE \"ID_Product\" = " + product.getId());
				ResultSet rs = pst.executeQuery()) {
			if (rs.next()) {
				product.setSurvey(SurveyDAO.getSurvey(rs.getInt(1)));
			}
        } catch (SQLException ex) {

            System.out.println(ex.getMessage());
        }
	}
	public static int getProductID(Product product) {
		int id = -1;
		Connection con=ConnectionDAO.getInstance().getConnection();
		try (PreparedStatement pst = con.prepareStatement("SELECT \"ID_Product\" FROM  \"Products\" WHERE \"Name\" = '" + product.getName()+"'");
				ResultSet rs = pst.executeQuery()) {
			if (rs.next()) {
				id = rs.getInt(1);
			}
        } catch (SQLException ex) {

            System.out.println(ex.getMessage());
        
        }
		return id;
	}
	public static int getSurveyID(Product product) {
		int id = -1;
		Connection con=ConnectionDAO.getInstance().getConnection();
		try (PreparedStatement pst = con.prepareStatement("SELECT \"ID_Survey\" FROM  \"Products\" WHERE \"Name\" = '" + product.getName()+"'");
				ResultSet rs = pst.executeQuery()) {
			if (rs.next()) {
				id = rs.getInt(1);
			}
        } catch (SQLException ex) {

            System.out.println(ex.getMessage());
        
        }
		return id;
	}
	public static int refreshScore(Product product) {
		int valor = 0;
		Connection con=ConnectionDAO.getInstance().getConnection();
		try (PreparedStatement pst = con.prepareStatement("SELECT AVG(\"Score_Product\") FROM public.\"Reviews\" WHERE \"ID_Product\" = " + ProductDAO.getProductID(product));
			 ResultSet rs = pst.executeQuery()) {
				if(rs.next()) {
					valor = (int) rs.getFloat(1);
					product.setScore(valor);
				}
		} catch (SQLException ex) {

            System.out.println(ex.getMessage());
        }
		return valor;
	}
		
	
	public static void main(String[] args) {
		
		
		ArrayList<Product> lista=new ArrayList<Product>();
		ProductDAO.getProductBasicInfo(lista);
		
		
		 for (Product producto : lista) {			
			System.out.println(producto);
		}
		
	
	}

}
