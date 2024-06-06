package core;

import dao.IDao;

public class ExpiryCollector implements Runnable{
    private final IDao dao;

    public ExpiryCollector(IDao dao) {
        this.dao = dao;
    }

    @Override
    public void run() {
        System.out.println("Cleaning cache...");
        dao.collect();
    }
}
