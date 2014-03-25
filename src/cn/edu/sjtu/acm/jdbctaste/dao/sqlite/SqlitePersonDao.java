package cn.edu.sjtu.acm.jdbctaste.dao.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import cn.edu.sjtu.acm.jdbctaste.dao.PersonDao;
import cn.edu.sjtu.acm.jdbctaste.entity.Person;

public class SqlitePersonDao implements PersonDao {

	public static final int IDX_ID = 1, IDX_NAME = 2, IDX_EMAIL = 3;

	private final Connection conn;

	public SqlitePersonDao(Connection conn) {
		this.conn = conn;
	}

	@Override
	public int insertPerson(Person person) {

		int ret = -1;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"insert into person (name, email) values (?,?);",
					Statement.RETURN_GENERATED_KEYS);
			stat.setString(1, person.getName());
			stat.setString(2, person.getEmail());

			stat.executeUpdate();
			
			ResultSet rs = stat.getGeneratedKeys();
			
			if (rs.next()) {
				int id = rs.getInt(1);
				person.setId(id);
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
	public boolean deletePerson(Person person) {
		// Auto-generated method stub
		boolean ret = false;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"delete from person where (id = ?);");
			stat.setInt(1, person.getId());
			stat.executeUpdate();
			stat.close();
			
			stat = conn.prepareStatement(
					"delete from joke where (speaker_id = ?);");
			stat.setInt(1, person.getId());
			stat.executeUpdate();
			stat.close();
			
			stat = conn.prepareStatement(
					"delete from comment where (commentator_id = ?);");
			stat.setInt(1, person.getId());
			stat.executeUpdate();
			stat.close();
			
			ret = true;
		} catch (SQLException e) {
			e.printStackTrace();
			ret = false;
		}

		return ret;
		// DONE
	}

	@Override
	public boolean updatePerson(Person person) {
		// Auto-generated method stub
		boolean ret = false;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"update person set name = ? where id = ?;");
			stat.setString(1, person.getName());
			stat.setString(2, person.getId().toString());
			stat.executeUpdate();
			stat.close();
			
			stat = conn.prepareStatement(
					"update person set email = ? where id = ?;");
			stat.setString(1, person.getEmail());
			stat.setString(2, person.getId().toString());
			stat.executeUpdate();
			stat.close();
			
			ret = true;
		} catch (SQLException e) {
			e.printStackTrace();
			ret = false;
		}

		return ret;
		// DONE
	}

	@Override
	public Person findPersonByEmail(String email) {
		// Auto-generated method stub
		Person ret = null;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"select * from person where email = ?;");
			stat.setString(1, email);
			
			ResultSet rs = stat.executeQuery();
			
			if (rs.next()) {
				ret = new Person(rs.getInt(1), rs.getString(2), rs.getString(3));
			}

			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			ret = null;
		}

		return ret;
		//DONE
	}

	@Override
	public int getNumOfJokes(Person person) {
		// TODO Auto-generated method stub

		int ret = 0;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"select * from joke where speaker_id = ?;");
			stat.setInt(1, person.getId());
			
			ResultSet rs = stat.executeQuery();
			
			if (rs.next()) {
				++ret;
				int joke_id = rs.getInt(1);
			}

			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			ret = -1;
		}

		return ret;
		//DONE
	}

	@Override
	public List<Person> getAllPerson() {
		List<Person> ret = new LinkedList<Person>();

		Statement stat;
		try {
			stat = conn.createStatement();

			stat.execute("select * from person;");
			ResultSet result = stat.getResultSet();

			while (result.next()) {
				ret.add(new Person(result.getInt(IDX_ID), result
						.getString(IDX_NAME), result.getString(IDX_EMAIL)));
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
	public Person findPersonById(int id) {
		// TODO Auto-generated method stub
		Person ret = null;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"select * from person where id = ?;");
			stat.setInt(1, id);
			
			ResultSet rs = stat.executeQuery();
			
			if (rs.next()) {
				ret = new Person(rs.getInt(1), rs.getString(2), rs.getString(3));
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
