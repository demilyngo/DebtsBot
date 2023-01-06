package dao;

import Entities.DebtEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import Utils.HibernateSessionFactoryUtil;

import java.util.List;

public class DebtDao {
    public DebtEntity findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(DebtEntity.class, id);
    }

    public List<DebtEntity> findByReceiver(String receiver) {
        List<DebtEntity> debts = HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("FROM DebtEntity WHERE receiver = :receiver").setParameter("receiver", receiver).list();
        return debts;
    }

    public List<DebtEntity> findByDebtor(String debtor) {
        List<DebtEntity> debts = HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("FROM DebtEntity WHERE debtor = :debtor").setParameter("debtor", debtor).list();
        return debts;
    }

    public void save(DebtEntity debt) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.save(debt);
        t.commit();
        session.close();
    }

    public void update(DebtEntity debt) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.update(debt);
        t.commit();
        session.close();
    }

    public void delete(DebtEntity debt) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        session.delete(debt);
        t.commit();
        session.close();
    }

    public List<DebtEntity> findAll() {
        List<DebtEntity> debts = (List<DebtEntity>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From DebtEntity ").list();

        return debts;
    }

}
