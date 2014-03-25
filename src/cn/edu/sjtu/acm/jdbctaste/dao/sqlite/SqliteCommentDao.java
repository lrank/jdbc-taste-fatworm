package cn.edu.sjtu.acm.jdbctaste.dao.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

import cn.edu.sjtu.acm.jdbctaste.dao.CommentDao;
import cn.edu.sjtu.acm.jdbctaste.entity.Comment;
import cn.edu.sjtu.acm.jdbctaste.entity.Joke;
import cn.edu.sjtu.acm.jdbctaste.entity.Person;

public class SqliteCommentDao implements CommentDao {

	private final Connection conn;
	
	public static final int IDX_ID = 1, IDX_BODY = 2, IDX_JOKE = 3, IDX_COMMENTATOR = 4, IDX_POST_TIME = 5;
	
	public SqliteCommentDao(Connection conn) {
		this.conn = conn;
	}

	@Override
	public int insertComment(Comment comment) {
		// Auto-generated method stub

		int ret = -1;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"insert into comment (joke_id, commentator_id, body, posttime) values (?,?,?,?);",
					Statement.RETURN_GENERATED_KEYS);
			stat.setInt(1, comment.getJoke().getId());
			stat.setInt(2, comment.getCommentator().getId());
			stat.setString(3, comment.getBody());
			stat.setTimestamp(4, comment.getPostTime());
			
			stat.executeUpdate();
			
			ResultSet rs = stat.getGeneratedKeys();
			
			if (rs.next()) {
				int id = rs.getInt(1);
				comment.setId(id);
				ret = id;
			}

			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			ret = -1;
		}

		return ret;
	}

	@Override
	public boolean deleteComment(Comment comment) {
		// Auto-generated method stub
		boolean ret = false;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"delete from comment where (id = ?);");
			stat.setInt(1, comment.getId());

			stat.executeUpdate();
			
			ret = true;
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			ret = false;
		}

		return ret;
	}

	@Override
	public boolean updateComment(Comment comment) {
		// Auto-generated method stub
		boolean ret = false;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"update comment set joke_id = ? where id = ?;");
			stat.setInt(1, comment.getJoke().getId());
			stat.setInt(2, comment.getId());
			stat.executeUpdate();
			stat.close();
			
			stat = conn.prepareStatement(
					"update comment set commentator_id = ? where id = ?;");
			stat.setInt(1, comment.getCommentator().getId());
			stat.setInt(2, comment.getId());
			stat.executeUpdate();
			stat.close();
			
			stat = conn.prepareStatement(
					"update comment set body = ? where id = ?;");
			stat.setString(1, comment.getBody());
			stat.setInt(2, comment.getId());
			stat.executeUpdate();
			stat.close();
			
			stat = conn.prepareStatement(
					"update comment set posttime = ? where id = ?;");
			stat.setTimestamp(1, comment.getPostTime());
			stat.setInt(2, comment.getId());
			stat.executeUpdate();
			stat.close();
			
			ret = true;
		} catch (SQLException e) {
			e.printStackTrace();
			ret = false;
		}

		return ret;
	}

	@Override
	public List<Comment> findCommentsOfPerson(Person person) {
		// Auto-generated method stub
		List<Comment> ret = new ArrayList<Comment>();

		try {
			PreparedStatement stat = conn.prepareStatement(
					"select * from comment, person, joke " +
						"where comment.joke_id = joke.id and comment.commentator_id = person.id and "
						+ "comment.commentator_id = ?;");
			stat.setInt(1, person.getId());
			
			ResultSet rs = stat.executeQuery();
			
			while (rs.next()) {
				Person p = new Person(rs.getInt(6), rs.getString(7), rs.getString(8));
				ret.add(new Comment(rs.getInt(1),
								new Joke(rs.getInt(9), p, rs.getString(11), rs.getTimestamp(12), rs.getInt(13)),
								p,
								rs.getString(4),
								rs.getTimestamp(5)));
			}

			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			ret = null;
		}

		return ret;
	}

	@Override
	public List<Comment> findCommentsReceived(Person person) {
		// Auto-generated method stub
		List<Comment> ret = new ArrayList<Comment>();

		try {
			PreparedStatement stat = conn.prepareStatement(
					"select * from comment, person, joke " +
						"where comment.joke_id = joke.id and comment.commentator_id = person.id and "
						+ "joke.speaker_id = ?;");
			stat.setInt(1, person.getId());
			
			ResultSet rs = stat.executeQuery();
			
			while (rs.next()) {
				Person p = new Person(rs.getInt(6), rs.getString(7), rs.getString(8));
				ret.add(new Comment(rs.getInt(1),
								new Joke(rs.getInt(9), p, rs.getString(11), rs.getTimestamp(12), rs.getInt(13)),
								p,
								rs.getString(4),
								rs.getTimestamp(5)));
			}

			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			ret = null;
		}

		return ret;
	}

	@Override
	public List<Comment> findCommentsOfJoke(Joke joke) {
		// Auto-generated method stub
		List<Comment> ret = new ArrayList<Comment>();

		try {
			PreparedStatement stat = conn.prepareStatement(
					"select * from comment, person, joke " +
						"where comment.joke_id = joke.id and comment.commentator_id = person.id and "
						+ "joke.id = ?;");
			stat.setInt(1, joke.getId());
			
			ResultSet rs = stat.executeQuery();
			
			while (rs.next()) {
				Person p = new Person(rs.getInt(6), rs.getString(7), rs.getString(8));
				
				PreparedStatement stat_find = conn.prepareStatement(
						"select * from person, joke where joke.speaker_id = person.id and joke.id = ?;");
				stat_find.setInt(1, joke.getId());
				ResultSet rs_find = stat_find.executeQuery();
				
				if (rs_find.next()) {
					Person joke_p = new Person(rs_find.getInt(1), rs_find.getString(2), rs_find.getString(3));
					ret.add(new Comment(rs.getInt(1),
								new Joke(rs.getInt(9), joke_p, rs.getString(11), rs.getTimestamp(12), rs.getInt(13)),
								p,
								rs.getString(4),
								rs.getTimestamp(5)));
				}
				stat_find.close();
				rs_find.close();
			}

			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			ret = null;
		}

		return ret;
	}

	@Override
	public List<Comment> getAllComments() {
		//Auto-generated method stub
		List<Comment> ret = new ArrayList<Comment>();

		try {
			PreparedStatement stat = conn.prepareStatement(
					"select * from comment, person, joke " +
						"where comment.joke_id = joke.id and comment.commentator_id = person.id;");
			
			ResultSet rs = stat.executeQuery();
			
			while (rs.next()) {
				Person p = new Person(rs.getInt(6), rs.getString(7), rs.getString(8));
				ret.add(new Comment(rs.getInt(1),
								new Joke(rs.getInt(9), p, rs.getString(11), rs.getTimestamp(12), rs.getInt(13)),
								p,
								rs.getString(4),
								rs.getTimestamp(5)));
			}

			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			ret = null;
		}

		return ret;
	}

	@Override
	public Comment findCommentById(int id) {
		// TODO Auto-generated method stub
		Comment ret = null;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"select * from comment, person, joke " +
						"where comment.joke_id = joke.id and comment.commentator_id = person.id and "
						+ "comment.id = ?;");
			stat.setInt(1, id);
			
			ResultSet rs = stat.executeQuery();
			
			while (rs.next()) {
				Person p = new Person(rs.getInt(6), rs.getString(7), rs.getString(8));
				ret = new Comment(rs.getInt(1),
								new Joke(rs.getInt(9), p, rs.getString(11), rs.getTimestamp(12), rs.getInt(13)),
								p,
								rs.getString(4),
								rs.getTimestamp(5));
			}

			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			ret = null;
		}

		return ret;
	}
}
