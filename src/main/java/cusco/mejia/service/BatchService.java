package cusco.mejia.service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import cusco.mejia.dto.BookDto;
import cusco.mejia.repository.BookRepository;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class BatchService {

    @Inject
    BookRepository bookRepository;

    
    @Scheduled(every = "20s")
    public void process() {
        log.info("Processing...");
        processBooks();
    }

    private void processBooks() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<BookDto> books = bookRepository.getAllBooks();
        log.info("Total books size: {}", books.size());
        List<List<BookDto>> subLists = splitListInSublists(books);
        log.info("Total sublists size: {}", subLists.size());
        subLists.forEach(subList -> {
            executorService.execute(new WorkerThread(subList, bookRepository));
        });
        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }
    }


    // split the list in chunks of 10 elements
    private static List<List<BookDto>> splitListInSublists(List<BookDto> books) {
        List<List<BookDto>> subLists = new ArrayList<>();
        int size = books.size();
        int max = 10;
        for (int i = 0; i < size; i += max) {
            subLists.add(books.subList(i, Math.min(size, i + max)));
        }
        return subLists;
    }

    // implement garbage collector to delete books of the list
    // Runtime runtime =  Runtime.getRuntime();
    // long memory = runtime.totalMemory() -  runtime.freeMemory();
    // double usedMemoryInMB = (double) memory / (1024 * 1024);
    // log.info("Memory used: {}", usedMemoryInMB);
    // books = null;
    // System.gc();
    // // get memory after garbage collector
    // memory = runtime.totalMemory() -  runtime.freeMemory();
    // usedMemoryInMB = (double) memory / (1024 * 1024);
    // log.info("Memory used after GC: {}", usedMemoryInMB);

}
