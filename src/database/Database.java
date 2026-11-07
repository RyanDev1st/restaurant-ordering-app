package database;

import java.util.List;

// The 5 methods every database class must have. T is the model class, for example MenuItem.
public interface Database<T> {
    List<T> findAll();       // every row in the table
    T findById(int id);      // one row, or null if it is not there
    int insert(T item);      // adds a row and gives back its new id
    void update(T item);     // changes a row
    void delete(int id);     // removes a row
}
