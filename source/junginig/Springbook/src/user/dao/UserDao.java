package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;
import user.domain.User;

import javax.sql.DataSource;


public class UserDao {
    private DataSource dataSource;
    private JdbcContext jdbcContext;
    public void setDataSource(DataSource d){
        this.jdbcContext = new JdbcContext();
        this.jdbcContext.setDataSource(d);
        this.dataSource = d;
    }
    public void add(final User user) throws SQLException {
        jdbcContext.workWithStatementStrategy( new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement(
                        "insert into users(id, name, password) values(?,?,?)");

                ps.setString(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());
                ps.executeUpdate();
                return ps;
            }
        });
    }

    public User get(String id) throws SQLException {
        Connection c = dataSource.getConnection();


        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;
        if(rs.next()){
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if(user == null) throw new EmptyResultDataAccessException(1);
        return user;
    }


    public void deleteAll() throws SQLException{
        jdbcContext.workWithStatementStrategy((new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
               return c.prepareStatement("delete from users");
            }
        }));
    }

    public int getCount() throws SQLException  {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement("select count(*) from users");

            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch(SQLException e){
            throw e;
        } finally {
            if(rs!=null){
                try{
                    rs.close();
                } catch (SQLException e){ }
            }
            if(ps!=null){
                try{
                    ps.close();
                } catch (SQLException e){ }
            }
            if(c!=null) {
                try {
                    c.close();
                } catch (SQLException e) { }
            }
        }
    }
}