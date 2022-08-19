package main;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UploadSqlData {


    private static SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            if (sessionFactory == null) {
                StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
                Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            }
            return sessionFactory;
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    public static void uploadData() throws IOException {
        Session session = getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        List<Page> pageList = Main.getAllPages();
        for (Page page1 : pageList) {
            Map<String, Integer> lemmaMap = Main.getAllLemmas();
            for (Map.Entry<String, Integer> entry : lemmaMap.entrySet()) {
                Lemma lemma = new Lemma();
                lemma.setLemma(entry.getKey());
                lemma.setFrequency(entry.getValue());
                if (page1.getContent().contains(entry.getKey())) {
                    session.save(page1);
                    session.save(lemma);
                    SearchIndex index = new SearchIndex();
                    index.setPage(page1);
                    index.setLemma(lemma);
                    session.save(index);
                }
            }
        }
        Field titleField = new Field();
        titleField.setName("title");
        titleField.setSelector("title");
        titleField.setWeight(1.0f);
        session.save(titleField);
        Field bodyField = new Field();
        bodyField.setName("body");
        bodyField.setSelector("body");
        bodyField.setWeight(0.8f);
        session.save(bodyField);
        transaction.commit();
        session.close();
    }
}
