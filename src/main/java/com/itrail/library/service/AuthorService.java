package com.itrail.library.service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.itrail.library.domain.Author;
import com.itrail.library.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorService {
    
    private final AuthorRepository authorRepository;

    /**
     * Поиск по ФИО
     * @param fio - ФИО автора
     * @return List Author
     */
    public List<Author> getAuthors( String fio ){
        return authorRepository.findAuthorsByFio(fio); 
    }

    public List<Author> getAllAuthors( int page, int size ){
        return authorRepository.findAll(PageRequest.of( page - 1, size ))
                               .stream()
                               .toList();
    }

}
