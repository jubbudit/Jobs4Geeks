/*
 * Created by Conner Owens on 2021.11.5
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.CompanyUser;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class CompanyUserFacade extends AbstractFacade<CompanyUser> {
    /*
    ---------------------------------------------------------------------------------------------
    The EntityManager is an API that enables database CRUD (Create Read Update Delete) operations
    and complex database searches. An EntityManager instance is created to manage entities
    that are defined by a persistence unit. The @PersistenceContext annotation below associates
    the entityManager instance with the persistence unitName identified below.
    ---------------------------------------------------------------------------------------------
     */
    @PersistenceContext(unitName = "HealthSurvey-OwensPU")
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
    public CompanyUserFacade() {
        super(CompanyUser.class);
    }

    /*
     *********************
     *   Other Methods   *
     *********************
     */

    // Returns the object reference of the CompanyUser object whose primary key is id
    public CompanyUser getUser(int id) {
        // The find method is inherited from the parent AbstractFacade class
        return entityManager.find(CompanyUser.class, id);
    }

    // Returns the object reference of the CompanyUser object whose user name is username
    public CompanyUser findByUsername(String username) {
        if (entityManager.createQuery("SELECT c FROM CompanyUser c WHERE c.username = :username")
                .setParameter("username", username)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (CompanyUser) (entityManager.createQuery("SELECT c FROM CompanyUser c WHERE c.username = :username")
                    .setParameter("username", username)
                    .getSingleResult());
        }
    }

    // Deletes the CompanyUser entity object whose primary key is id
    public void deleteUser(int id) {

        // The find method is inherited from the parent AbstractFacade class
        CompanyUser user = entityManager.find(CompanyUser.class, id);

        // The remove method is inherited from the parent AbstractFacade class
        entityManager.remove(user);
    }

}
