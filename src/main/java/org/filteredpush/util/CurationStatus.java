package org.filteredpush.util;

/**
* Created with IntelliJ IDEA.
* User: cobalt
* Date: 30.05.2013
* Time: 15:50
* To change this template use File | Settings | File Templates.
*/
public class CurationStatus {
    private String status;

    public CurationStatus(String msg){
        status = msg;
    }

    public String toString(){
        return status;
    }
}
