package com.example.copia.DatabaseOperation;

import android.content.Context;

import com.example.copia.FileCompressor;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FileUpload
{
    public boolean client_file_upload(final ParseObject reference, ArrayList<NormalFile> filesList, Context context)
    {
        ArrayList<Boolean> results = new ArrayList<>();
        if(filesList.size() > 0)
        {
            ArrayList<Callable<Boolean>> taskList = new ArrayList<>();
            List<Future<Boolean>> callableList = new ArrayList<>();
            ExecutorService es = Executors.newFixedThreadPool(5);
            for (final NormalFile file : filesList)
            {
                Callable<Boolean> callable = new Callable<Boolean>()
                {
                    private boolean finish = false;
                    private boolean successful = false;
                    @Override
                    public Boolean call() throws Exception
                    {
                        File normalFile = new File(file.getPath());
                        ParseObject query = new ParseObject("PDFFiles");
                        query.put("Parent", reference.getObjectId());
                        query.put("ClientPointer", reference);
                        query.put("Name", file.getName());
                        try {
                            query.put("Files", new ParseFile(normalFile.getName(), FileUtils.readFileToByteArray(normalFile)));
                        }
                        catch (IOException e) {e.printStackTrace();}
                        query.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null)
                                    successful = true;
                                finish = true;

                            }
                        });

                        while(finish == false)
                            Thread.sleep(1000);

                        return successful;
                    }
                };
                taskList.add(callable);
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
            results.add(true);
        return results.contains(false);
    }

    public boolean suppliers_file_upload(final ParseObject reference, ArrayList<NormalFile> filesList)
    {
        ArrayList<Boolean> results = new ArrayList<>();
        if(filesList.size() > 0)
        {
            ArrayList<Callable<Boolean>> taskList = new ArrayList<>();
            List<Future<Boolean>> callableList = new ArrayList<>();
            ExecutorService es = Executors.newFixedThreadPool(5);
            for (final NormalFile file : filesList)
            {
                Callable<Boolean> callable = new Callable<Boolean>()
                {
                    private boolean finish = false;
                    private boolean successful = false;
                    @Override
                    public Boolean call() throws Exception
                    {
                        File normalFile = new File(file.getPath());
                        ParseObject query = new ParseObject("PDFFiles");
                        query.put("SuppliersPointer", reference);
                        query.put("Parent", reference.getObjectId());
                        query.put("Name", file.getName());
                        try {
                            query.put("Files", new ParseFile(normalFile.getName(), FileUtils.readFileToByteArray(normalFile)));
                        }
                        catch (IOException e) {e.printStackTrace();}
                        query.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null)
                                    successful = true;
                                finish = true;

                            }
                        });

                        while(finish == false)
                            Thread.sleep(1000);

                        return successful;
                    }
                };
                taskList.add(callable);
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
            results.add(true);
        return results.contains(false);
    }

    public boolean contractors_file_upload(final ParseObject reference, ArrayList<NormalFile> filesList)
    {
        ArrayList<Boolean> results = new ArrayList<>();
        if(filesList.size() > 0)
        {
            ArrayList<Callable<Boolean>> taskList = new ArrayList<>();
            List<Future<Boolean>> callableList = new ArrayList<>();
            ExecutorService es = Executors.newFixedThreadPool(5);
            for (final NormalFile file : filesList)
            {
                Callable<Boolean> callable = new Callable<Boolean>()
                {
                    private boolean finish = false;
                    private boolean successful = false;
                    @Override
                    public Boolean call() throws Exception
                    {
                        File normalFile = new File(file.getPath());
                        ParseObject query = new ParseObject("PDFFiles");
                        query.put("ContractorsPointer", reference);
                        query.put("Parent", reference.getObjectId());
                        query.put("Name", file.getName());
                        try {
                            query.put("Files", new ParseFile(normalFile.getName(), FileUtils.readFileToByteArray(normalFile)));
                        }
                        catch (IOException e) {e.printStackTrace();}
                        query.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null)
                                    successful = true;
                                finish = true;

                            }
                        });

                        while(finish == false)
                            Thread.sleep(1000);

                        return successful;
                    }
                };
                taskList.add(callable);
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
            results.add(true);
        return results.contains(false);
    }
    public boolean consultants_file_upload(final ParseObject reference, ArrayList<NormalFile> filesList)
    {
        ArrayList<Boolean> results = new ArrayList<>();
        if(filesList.size() > 0)
        {
            ArrayList<Callable<Boolean>> taskList = new ArrayList<>();
            List<Future<Boolean>> callableList = new ArrayList<>();
            ExecutorService es = Executors.newFixedThreadPool(5);
            for (final NormalFile file : filesList)
            {
                Callable<Boolean> callable = new Callable<Boolean>()
                {
                    private boolean finish = false;
                    private boolean successful = false;
                    @Override
                    public Boolean call() throws Exception
                    {
                        File normalFile = new File(file.getPath());
                        ParseObject query = new ParseObject("PDFFiles");
                        query.put("ConsultantsPointer", reference);
                        query.put("Parent", reference.getObjectId());
                        query.put("Name", file.getName());
                        try {
                            query.put("Files", new ParseFile(normalFile.getName(), FileUtils.readFileToByteArray(normalFile)));
                        }
                        catch (IOException e) {e.printStackTrace();}
                        query.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null)
                                    successful = true;
                                finish = true;

                            }
                        });

                        while(finish == false)
                            Thread.sleep(1000);

                        return successful;
                    }
                };
                taskList.add(callable);
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
            results.add(true);
        return results.contains(false);
    }

    public boolean specifications_file_upload(final ParseObject reference, ArrayList<NormalFile> filesList)
    {
        ArrayList<Boolean> results = new ArrayList<>();
        if(filesList.size() > 0)
        {
            ArrayList<Callable<Boolean>> taskList = new ArrayList<>();
            List<Future<Boolean>> callableList = new ArrayList<>();
            ExecutorService es = Executors.newFixedThreadPool(5);
            for (final NormalFile file : filesList)
            {
                Callable<Boolean> callable = new Callable<Boolean>()
                {
                    private boolean finish = false;
                    private boolean successful = false;
                    @Override
                    public Boolean call() throws Exception
                    {
                        File normalFile = new File(file.getPath());
                        ParseObject query = new ParseObject("PDFFiles");
                        query.put("SpecificationsPointer", reference);
                        query.put("Parent", reference.getObjectId());
                        query.put("Name", file.getName());
                        try {
                            query.put("Files", new ParseFile(normalFile.getName(), FileUtils.readFileToByteArray(normalFile)));
                        }
                        catch (IOException e) {e.printStackTrace();}
                        query.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null)
                                    successful = true;
                                finish = true;

                            }
                        });

                        while(finish == false)
                            Thread.sleep(1000);

                        return successful;
                    }
                };
                taskList.add(callable);
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
            results.add(true);
        return results.contains(false);
    }
}
