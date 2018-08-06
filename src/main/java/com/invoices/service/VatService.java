package com.invoices.service;

import com.invoices.domain.Vat;
import com.invoices.enumerations.IsApplicable;
import com.invoices.repository.VatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author psoutzis
 * This class is annotated as a service.
 * It is the service bean for the Vat entity
 */
@Service
public class VatService {

    @Autowired private VatRepo vatRepo;

    /**
     * This method is used when updating an invoice.
     * @param rate The value of the rate to insert to DB. This will be null if user does not enter rate manually
     * @return The appropriate Vat record to update the invoice with
     */
    public Vat getRecordAndSaveIfNotExists(String rate){
        Vat vat = getRecordWithRateOf(Float.valueOf(rate));

        return (vat != null ? vat : save(new Vat(null, IsApplicable.YES, Float.valueOf(rate) )));
    }

    /**
     * This method will check if the user selected vat to be applicable or not.
     * If the user selected 'NO', as to 'Not Applicable', then whatever the
     * vat rate, the method will return the rate with a value of zero (0).
     * @param isApplicable if VAT is applicable to the issuing invoice.
     * @param id the unique id of the record holding the selected rate.
     * @return The appropriate 'vat' record from the database, based on the user's selections
     */
    public Vat determineVat(IsApplicable isApplicable, Long id){

        return isApplicable == IsApplicable.NO ? getRecordWithRateOfZero() : getRecord(id);
    }

    /**
     * Method will insert the parsed Vat object to the database
     * @param vat the Vat object to insert to database as a record
     * @return the Vat object that was just inserted to db (and now has an autogenerated ID)
     */
    private Vat save(Vat vat){

        return vatRepo.save(vat);
    }

    /**
     * @return all the Vat records from the database
     */
    public List<Vat> getVatRecords(){

        return vatRepo.findAll();
    }

    /**
     * @param id the id of vat record to fetch
     * @return a Vat record with a unique id, identical to the one passed as an argument
     */
    private Vat getRecord(Long id){

        return vatRepo.findVatByVatId(id);
    }


    /**
     * @return the record from the database that has a rate of 0.0%
     */
    private Vat getRecordWithRateOfZero(){

        return vatRepo.findVatByVatRate(0F);
    }

    private Vat getRecordWithRateOf(Float rate){
        try {
            return vatRepo.findByAccurateVatRate(rate-0.0001f, rate+0.0001f);
        }
        catch(NullPointerException e){
            System.out.println("Record with rate of '"+rate+"' does not exist in database.");
            return null;
        }
    }

}
