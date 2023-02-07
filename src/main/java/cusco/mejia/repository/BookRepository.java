package cusco.mejia.repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import cusco.mejia.datasource.Jdbc;
import cusco.mejia.dto.BookDto;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class BookRepository {

    @Inject
    Jdbc jdbc;


    private static final String SQL_SELECT = "select id, title, author, isbn, price from books";
    private static final String SQL_UPDATE = "update books set title = ? where id = ?";

    public List<BookDto> getAllBooks() {
        Connection connection = jdbc.getConnection();
        List<BookDto> books = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BookDto book = new BookDto();
                book.setId(rs.getLong("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPrice(rs.getBigDecimal("price"));
                books.add(book);
            }
        } catch (Exception e) {
            log.error("Error al obtener los libros", e);
        } finally{
            jdbc.closeConnection(connection);
        }
        log.info("Libros obtenidos: {}", books.size());
        return books;
    }

    public boolean updateBook(Long id, String title) {
        Connection connection =  jdbc.getConnection();
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, title);
            ps.setLong(2, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                log.info("Libro actualizado: {}", id);
                return true;
            }
            return false;

        } catch (Exception e) {
            log.error("Error al obtener los libros", e);
        } finally{
            jdbc.closeConnection(connection);
        }
        return false;
    }
    
}
