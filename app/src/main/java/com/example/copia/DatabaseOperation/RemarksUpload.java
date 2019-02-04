package com.example.copia.DatabaseOperation;

import com.dpizarro.autolabel.library.Label;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RemarksUpload
{
    ArrayList<Callable<Boolean>> taskList = new ArrayList<>();
    List<Future<Boolean>> callableList = new ArrayList<>();
    ExecutorService es = Executors.newFixedThreadPool(5);

    public void client_remarks_upload(List<Label> remarksList, final ParseObject reference)
    {
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
            } catch (InterruptedException e) {
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
    }
}