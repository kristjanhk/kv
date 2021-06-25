package eu.kyngas.kv.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

public abstract class BaseDatabaseTest extends BaseTest {
  @Inject
  TransactionManager transactionManager;

  @BeforeEach
  void beginTransaction() throws SystemException, NotSupportedException {
    transactionManager.begin();
  }

  @AfterEach
  void rollbackTransaction() throws SystemException {
    transactionManager.rollback();
  }
}
