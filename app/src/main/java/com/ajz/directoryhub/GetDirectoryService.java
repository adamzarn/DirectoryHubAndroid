package com.ajz.directoryhub;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by adamzarn on 11/12/17.
 */

public class GetDirectoryService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        new FirebaseClient().getDirectory(job.getTag());
        jobFinished(job, true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

}
