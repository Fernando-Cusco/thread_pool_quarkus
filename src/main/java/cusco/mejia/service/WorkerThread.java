package cusco.mejia.service;

import java.util.List;

import cusco.mejia.dto.BookDto;
import cusco.mejia.http.RestClient;
import cusco.mejia.repository.BookRepository;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkerThread implements Runnable {

    private List<BookDto> books;
    private Object lock = new Object();

    // inject repository
    private final BookRepository bookRepository;
    private final RestClient restClient;

    public WorkerThread(List<BookDto> books, BookRepository bookRepository, RestClient restClient) {
        this.books = books;
        this.bookRepository = bookRepository;
        this.restClient = restClient;
    }

    @Override
    public void run() {
        log.info("Thread name: {}", Thread.currentThread().getName());
        log.info("Empezo a procesar, llega con: {}", books.size());
        synchronized (lock) {
            books.forEach(book -> {
                long start = System.currentTimeMillis();
                log.info("Objeto actual: {}, hilo contenedor: {}", book.getId(), Thread.currentThread().getName());
                JsonObject res = restClient.sendRequest("https://reqres.in/api/users/2");
                log.info("Response: {}", res);
                String data = res.getJsonObject("data").getString("first_name");
                boolean bookUpdated = bookRepository.updateBook(book.getId(), Thread.currentThread().getName().concat("-").concat(book.getId()+"-"+data));
                if (bookUpdated) {
                    log.info("Book updated: {}", book.getId());
                } else {
                    log.info("Book not updated: {}", book.getId());
                }
                long end = System.currentTimeMillis();
                log.info("TIME FINISHED: {} ms {} {}", (end - start), book.getId(), Thread.currentThread().getName());
            });
        }
        
    }
}
