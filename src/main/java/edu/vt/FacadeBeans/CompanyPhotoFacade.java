/*
 * Created by Conner Owens on 2021.7.30
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.CompanyPhoto;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class CompanyPhotoFacade extends AbstractFacade<CompanyPhoto> {
    /*
    ---------------------------------------------------------------------------------------------
    The EntityManager is an API that enables database CRUD (Create Read Update Delete) operations
    and complex database searches. An EntityManager instance is created to manage entities
    that are defined by a persistence unit. The @PersistenceContext annotation below associates
    the entityManager instance with the persistence unitName identified below.
    ---------------------------------------------------------------------------------------------
     */
    @PersistenceContext(unitName = "Jobs4GeeksPU")
    private EntityManager entityManager;

    // Obtain the object reference of the EntityManager instance in charge of
    // managing the entities in the persistence context identified above.
    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /*
    This constructor method invokes its parent AbstractFacade's constructor method,
    which in turn initializes its entity class type T and entityClass instance variable.
     */
    public CompanyPhotoFacade() {
        super(CompanyPhoto.class);
    }

    /*
     *********************
     *   Other Methods   *
     *********************
     */

    // Returns a list of object references of CompanyPhoto objects that belong to the
    // User object whose primary key is primaryKey
    public List<CompanyPhoto> findPhotosByUserPrimaryKey(Integer primaryKey) {
        /*
        The following @NamedQuery definition is given in CompanyPhoto entity class file:
            @NamedQuery(name = "CompanyPhoto.findPhotosByUserDatabasePrimaryKey", query = "SELECT p FROM CompanyPhoto p WHERE p.userId.id = :primaryKey")

        userId.id --> User object's database primary key
        The following statement obtains the results from the named database query.
         */

        return (List<CompanyPhoto>) entityManager.createNamedQuery("CompanyPhoto.findPhotosByUserDatabasePrimaryKey")
                .setParameter("primaryKey", primaryKey)
                .getResultList();
    }

}
