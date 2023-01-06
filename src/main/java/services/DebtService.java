package services;

import dao.DebtDao;
import Entities.DebtEntity;
import java.util.List;

public class DebtService {
    private DebtDao debtDao = new DebtDao();

    public DebtService() {}

    public DebtEntity findDebt(int id) {
        return debtDao.findById(id);
    }

    public void saveDebt(DebtEntity debt) {
        debtDao.save(debt);
    }

    public void deleteDebt(DebtEntity debt) {
        debtDao.delete(debt);
    }

    public void updateDebt(DebtEntity debt) {
        debtDao.update(debt);
    }

    public List<DebtEntity> findAllDebts() {
        return debtDao.findAll();
    }

    public List<DebtEntity> findByReceiver(String receiver) {return debtDao.findByReceiver(receiver);}

    public List<DebtEntity> findByDebtor(String debtor) {return debtDao.findByDebtor(debtor);}
}
