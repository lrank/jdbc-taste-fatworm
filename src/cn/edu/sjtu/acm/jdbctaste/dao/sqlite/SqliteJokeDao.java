package cn.edu.sjtu.acm.jdbctaste.dao.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import cn.edu.sjtu.acm.jdbctaste.dao.JokeDao;
import cn.edu.sjtu.acm.jdbctaste.dao.PersonDao;
import cn.edu.sjtu.acm.jdbctaste.entity.Joke;
import cn.edu.sjtu.acm.jdbctaste.entity.Person;

public class SqliteJokeDao implements JokeDao {

	public static final int IDX_ID = 1, IDX_BODY = 3, IDX_SPEAKER = 2,
			IDX_POST_TIME = 4, IDX_ZAN = 5;

	private final Connection conn;

	public SqliteJokeDao(Connection conn) {
		this.conn = conn;
	}

	@Override
	public int insertJoke(Joke joke) {
		// TODO Auto-generated method stub
		int ret = -1;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"insert into joke (speaker_id, body, posttime, zan) values (?,?,?,?);",
					Statement.RETURN_GENERATED_KEYS);
			stat.setInt(1, joke.getSpeaker().getId());
			stat.setString(2, joke.getBody());
			stat.setTimestamp(3, joke.getPostTime());
			stat.setInt(4, joke.getZan());

			stat.executeUpdate();
			
			ResultSet rs = stat.getGeneratedKeys();
			
			if (rs.next()) {
				int id = rs.getInt(1);
				joke.setId(id);
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
	public boolean deleteJoke(Joke joke) {
		// Auto-generated method stub
		boolean ret = false;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"delete from joke where id = ?;");
			stat.setInt(1, joke.getId());

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
	public boolean updateJoke(Joke joke) {
		// Auto-generated method stub
		boolean ret = false;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"update joke set speaker_id = ? where id = ?;");
			stat.setInt(1, joke.getSpeaker().getId());
			stat.setInt(2, joke.getId());
			stat.executeUpdate();
			stat.close();
			
			stat = conn.prepareStatement(
					"update joke set body = ? where id = ?;");
			stat.setString(1, joke.getBody());
			stat.setInt(2, joke.getId());
			stat.executeUpdate();
			stat.close();
			
			stat = conn.prepareStatement(
					"update joke set posttime = ? where id = ?;");
			stat.setTimestamp(1, joke.getPostTime());
			stat.setInt(2, joke.getId());
			stat.executeUpdate();
			stat.close();
			
			stat = conn.prepareStatement(
					"update joke set zan = ? where id = ?;");
			stat.setInt(1, joke.getZan());
			stat.setInt(2, joke.getId());
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
	public List<Joke> findJokesOfPerson(Person person) {
		// TODO Auto-generated method stub

		List<Joke> ret = new LinkedList<Joke>();

		Statement stat;
		try {
			stat = conn.createStatement();

			stat.execute("select * from joke, person " +
						 "where joke.speaker_id = person.id and person.id = " + person.getId().toString() + ";");
			ResultSet result = stat.getResultSet();

			while (result.next()) {
				ret.add(new Joke(result.getInt(1),
								 new Person(result.getInt(2), result.getString(6), result.getString(7)),
								 result.getString(3),
								 result.getTimestamp(4),
								 result.getInt(5) ));
			}
			result.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}

	@Override
	public List<Joke> getAllJokes() {
		// Auto-generated method stub
		List<Joke> ret = new LinkedList<Joke>();

		Statement stat;
		try {
			stat = conn.createStatement();

			stat.execute("select * from joke, person where joke.speaker_id = person.id;");
			ResultSet result = stat.getResultSet();

			while (result.next()) {
				ret.add(new Joke(result.getInt(IDX_ID),
								 new Person(result.getInt(2), result.getString(6), result.getString(7)),
								 result.getString(3),
								 result.getTimestamp(4),
								 result.getInt(5) ));
			}
			result.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}

	@Override
	public Joke findJokeById(int id) {
		Joke ret = null;

		try {
			PreparedStatement stat = conn
					.prepareStatement("select * from joke where id = ?;");
			stat.setInt(1, id);
			ResultSet result = stat.executeQuery();
			if (!result.next())
				return null;

			PersonDao personDao = SqliteDaoFactory.getInstance().getPersonDao();
			Person speaker = personDao.findPersonById(result.getInt(IDX_SPEAKER));
			
			ret = new Joke(result.getInt(IDX_ID), speaker,
					result.getString(IDX_BODY),
					result.getTimestamp(IDX_POST_TIME), result.getInt(IDX_ZAN));
			
			result.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public List<Joke> findJokesWithZanMoreThan(int zan) {
		// TODO Auto-generated method stub
		List<Joke> ret = new LinkedList<Joke>();

		Statement stat;
		try {
			stat = conn.createStatement();

			stat.execute("select * from joke, person where joke.speaker_id = person.id and zan > " + zan + ";");
			ResultSet result = stat.getResultSet();

			while (result.next()) {
				ret.add(new Joke(result.getInt(IDX_ID),
								 new Person(result.getInt(2), result.getString(6), result.getString(7)),
								 result.getString(3),
								 result.getTimestamp(4),
								 result.getInt(5) ));
			}
			result.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}
}
