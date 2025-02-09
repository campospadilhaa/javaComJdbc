package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import db.DB;
import entities.Order;
import entities.OrderStatus;
import entities.Product;

public class Program {

	public static void main(String[] args) throws SQLException {
		
		Connection conn = DB.getConnection();
	
		Statement st = conn.createStatement();
			
		ResultSet rs = st.executeQuery("select * from tb_order o " +
									   " inner join tb_order_product op on op.order_id = o.id " +
									   " inner join tb_product p on p.id = op.product_id");

		Map<Long, Order> mapOrder = new HashMap<>();
		Map<Long, Product> mapProduct = new HashMap<>();
		while (rs.next()) {

			Long orderId = rs.getLong("order_id");
			if(mapOrder.get(orderId) == null) {

				Order o = instantiateOrder(rs);
				mapOrder.put(orderId, o);
			}

			Long productId = rs.getLong("product_id");
			if(mapProduct.get(productId) == null) {

				Product p = instantiateProduct(rs);
				mapProduct.put(productId, p);
			}

			mapOrder.get(orderId).getListaProduct().add(mapProduct.get(productId));
		}

		for (Long orderId : mapOrder.keySet()) {

			System.out.println(mapOrder.get(orderId));

			for (Product product : mapOrder.get(orderId).getListaProduct()) {
				System.out.println(product);
			}

			System.out.println();
		}
	}

	private static Product instantiateProduct(ResultSet rs) throws SQLException {

		Product p = new Product();
		p.setId(rs.getLong("product_id"));
		p.setName(rs.getString("name"));
		p.setDescription(rs.getString("description"));
		p.setImageUri(rs.getString("image_uri"));
		p.setPrice(rs.getDouble("price"));

		return p;
	}

	private static Order instantiateOrder(ResultSet rs) throws SQLException {

		Order o = new Order();
		o.setId(rs.getLong("order_id"));
		o.setLatitude(rs.getDouble("latitude"));
		o.setLongitude(rs.getDouble("longitude"));
		o.setMoment(rs.getTimestamp("moment").toInstant());
		o.setStatus(OrderStatus.values()[rs.getInt("status")]);

		return o;
	}
}