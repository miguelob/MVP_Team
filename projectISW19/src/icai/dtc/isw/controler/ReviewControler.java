package icai.dtc.isw.controler;

import icai.dtc.isw.dao.ReviewDAO;
import icai.dtc.isw.domain.Review;


public class ReviewControler {
	public boolean uploadReview(Review review, int idProduct) {
		return ReviewDAO.uploadReview(review,idProduct);
	}
}
