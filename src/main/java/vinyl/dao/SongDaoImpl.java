package vinyl.dao;

import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vinyl.exception.DataProcessingException;
import vinyl.model.Song;

@Repository
public class SongDaoImpl implements SongDao {
    private SessionFactory sessionFactory;

    @Autowired
    public SongDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Song add(Song song) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(song);
            transaction.commit();
            return song;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't insert song " + song, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<Song> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Song> songQuery = session.createQuery("FROM Song WHERE id = :id", Song.class);
            songQuery.setParameter("id", id);
            return songQuery.uniqueResultOptional();
        } catch (Exception e) {
            throw new DataProcessingException("Can't get song by id " + id, e);
        }
    }
}
