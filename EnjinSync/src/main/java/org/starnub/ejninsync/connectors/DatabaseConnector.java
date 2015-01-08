package org.starnub.ejninsync.connectors;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.starnub.StarNub;
import org.starnub.database.ConnectionFactory;
import org.starnub.ejninsync.ejinusermanagment.AccountLink;

import java.sql.SQLException;
import java.util.Map;

public class DatabaseConnector {

    public ConnectionSource connection;
    private Dao<AccountLink, Integer> accountLinks;

    @SuppressWarnings("unchecked")
    public DatabaseConnector(String enjinSync, String dbType, String mysqlUrl, String mysqlUser, String mysqlPass){
        Map<String, Object> databaseConfig = (Map<String, Object>) StarNub.getPluginManager().getConfiguration("EnjinSync").get("database");
        connection = new ConnectionFactory().setConnection(enjinSync, dbType, mysqlUrl,  mysqlUser, mysqlPass);
    }

    public ConnectionSource getConnection() {
        return connection;
    }

    @SuppressWarnings("unchecked")
    public void createDao(){
        try {
            accountLinks = DaoManager.createDao(connection, AccountLink.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable(){
        try {
            TableUtils.createTableIfNotExists(connection, AccountLink.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<AccountLink, Integer> getAccountLinks() {
        return accountLinks;
    }

    public void setAccountLinks(Dao<AccountLink, Integer> accountLinks) {
        this.accountLinks = accountLinks;
    }

    public Dao<AccountLink, Integer> getAccounts() {
        return accountLinks;
    }

    public boolean createIfNotExistAccount(AccountLink accountLink){
        try {
            accountLinks.createIfNotExists(accountLink);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    public boolean createAccount(AccountLink accountLink){
        try {
            accountLinks.create(accountLink);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    public boolean createOrUpdateAccount(AccountLink accountLink){
        try {
            accountLinks.createOrUpdate(accountLink);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    public boolean updateAccount(AccountLink accountLink){
        try {
            accountLinks.update(accountLink);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    public boolean refreshAccount(AccountLink accountLink){
        try {
            accountLinks.refresh(accountLink);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    public boolean deleteAccount(AccountLink accountLink) {
        try {
            accountLinks.delete(accountLink);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    public AccountLink getAccountLinkFromStarNubIdEnjinIdUniqueCombo(int starnubId, String enjinId) {
        try {
            QueryBuilder<AccountLink, Integer> queryBuilder =
                    accountLinks.queryBuilder();
            Where<AccountLink, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("STARNUB_ID", starnubId)
                    .and()
                    .eq("ENJIN_ID", enjinId);
            PreparedQuery<AccountLink> preparedQuery = queryBuilder.prepare();
            return accountLinks.queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public AccountLink getAccountLinkByStarNubId(int starnubId) {
        try {
            QueryBuilder<AccountLink, Integer> queryBuilder =
                    accountLinks.queryBuilder();
            Where<AccountLink, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .like("STARNUB_ID", starnubId);
            PreparedQuery<AccountLink> preparedQuery = queryBuilder.prepare();
            return accountLinks.queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public AccountLink getAccountLinkByEnjinId(String enjinId) {
        try {
            QueryBuilder<AccountLink, Integer> queryBuilder =
                    accountLinks.queryBuilder();
            Where<AccountLink, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .like("ENJIN_ID", enjinId);
            PreparedQuery<AccountLink> preparedQuery = queryBuilder.prepare();
            return accountLinks.queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }
}