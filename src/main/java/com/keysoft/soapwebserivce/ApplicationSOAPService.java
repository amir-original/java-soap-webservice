package com.keysoft.soapwebserivce;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface ApplicationSOAPService {

    @WebMethod
    List<Application> getAll();

    @WebMethod
    Application getApplication(int id);

    @WebMethod
    boolean addApplication(Application app);

    @WebMethod
    boolean updateApplication(Application app);

    @WebMethod
    boolean delete(int id);
}
