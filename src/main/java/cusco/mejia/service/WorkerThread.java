package cusco.mejia.service;

import java.util.List;

import cusco.mejia.dto.BookDto;
import cusco.mejia.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkerThread implements Runnable {

    private List<BookDto> books;
    private Object lock = new Object();

    // inject repository
    private final BookRepository bookRepository;

    public WorkerThread(List<BookDto> books, BookRepository bookRepository) {
        this.books = books;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run() {
        log.info("Thread name: {}", Thread.currentThread().getName());
        log.info("Empezo a procesar, llega con: {}", books.size());
        synchronized (lock) {
            books.forEach(book -> {
                log.info("Objeto actual: {}, hilo contenedor: {}", book.getId(), Thread.currentThread().getName());
                boolean bookUpdated = bookRepository.updateBook(book.getId(), Thread.currentThread().getName().concat("-").concat(book.getId()+""));
                if (bookUpdated) {
                    log.info("Book updated: {}", book.getId());
                } else {
                    log.info("Book not updated: {}", book.getId());
                }
            });
        }
        
    }
}
