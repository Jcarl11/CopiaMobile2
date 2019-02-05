package com.example.copia.DatabaseOperation;

import com.dpizarro.autolabel.library.Label;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RemarksUpload
{
    ArrayList<Callable<Boolean>> taskList = new ArrayList<>();
    List<Future<Boolean>> callableList = new ArrayList<>();
    ExecutorService es = Executors.newFixedThreadPool(5);


    public boolean client_remarks_upload(List<Label> remarksList, final ParseObject reference)
    {
        ArrayList<Boolean> results = new ArrayList<>();
        if(remarksList.size() > 0)
        {
            for (Label label : remarksList) {
                final String remark = label.getText();
                Callable<Boolean> task = new Callable<Boolean>() {
                    private boolean finish = false;
                    private boolean successful = false;

                    @Override
                    public Boolean call() throws Exception {
                        ParseObject query2 = new ParseObject("Notes");
                        query2.put("Remark", remark);
                        query2.put("ClientPointer", reference);
                        query2.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null)
                                    successful = true;
                                finish = true;
                            }
                        });
                        while (finish == false)
                            Thread.sleep(1000);
                        return successful;
                    }
                };
                taskList.add(task);
            }

            try {
                callableList = es.invokeAll(taskList);
                for(Future<Boolean> future : callableList)
                    results.add(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            es.shutdown();
            try {
                if (!es.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    es.shutdownNow();
                }
            } catch (InterruptedException e) {
                es.shutdownNow();
            }
        }
        else
            results.add(false);
        return results.contains(false);
    }

    public boolean suppliers_remarks_upload(List<Label> remarksList, final ParseObject reference)
    {
        ArrayList<Boolean> results = new ArrayList<>();
        if(remarksList.size() > 0)
        {
            for (Label label : remarksList) {
                final String remark = label.getText();
                Callable<Boolean> task = new Callable<Boolean>() {
                    private boolean finish = false;
                    private boolean successful = false;

                    @Override
                    public Boolean call() throws Exception {
                        ParseObject query2 = new ParseObject("Notes");
                        query2.put("Remark", remark);
                        query2.put("SuppliersPointer", reference);
                        query2.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null)
                                    successful = true;
                                finish = true;
                            }
                        });
                        while (finish == false)
                            Thread.sleep(1000);
                        return successful;
                    }
                };
                taskList.add(task);
            }

            try {
                callableList = es.invokeAll(taskList);
                for(Future<Boolean> future : callableList)
                    results.add(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            es.shutdown();
            try {
                if (!es.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    es.shutdownNow();
                }
            } catch (InterruptedException e) {
                es.shutdownNow();
            }
        }
        else
            results.add(false);
        return results.contains(false);
    }
}
