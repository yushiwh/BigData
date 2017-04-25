package org.apache.hadoop.mapred;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.hbase.classification.InterfaceAudience;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.mapreduce.QueueAclsInfo;
import org.apache.hadoop.mapreduce.QueueInfo;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskCompletionEvent;
import org.apache.hadoop.mapreduce.TaskReport;
import org.apache.hadoop.mapreduce.protocol.ClientProtocol;
import org.apache.hadoop.mapreduce.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.mapreduce.v2.LogParams;
import org.apache.hadoop.mapreduce.v2.api.MRClientProtocol;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetDelegationTokenRequest;
import org.apache.hadoop.mapreduce.v2.jobhistory.JobHistoryUtils;
import org.apache.hadoop.mapreduce.v2.util.MRApps;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenSelector;
import org.apache.hadoop.yarn.util.ConverterUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class YARNRunner
        implements ClientProtocol
{
    private static final Log LOG = LogFactory.getLog(YARNRunner.class);
    private final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
    private ResourceMgrDelegate resMgrDelegate;
    private ClientCache clientCache;
    private Configuration conf;
    private final FileContext defaultFileContext;

    public YARNRunner(Configuration conf)
    {
        this(conf, new ResourceMgrDelegate(new YarnConfiguration(conf)));
    }

    public YARNRunner(Configuration conf, ResourceMgrDelegate resMgrDelegate)
    {
        this(conf, resMgrDelegate, new ClientCache(conf, resMgrDelegate));
    }

    public YARNRunner(Configuration conf, ResourceMgrDelegate resMgrDelegate, ClientCache clientCache)
    {
        this.conf = conf;
        try
        {
            this.resMgrDelegate = resMgrDelegate;
            this.clientCache = clientCache;
            this.defaultFileContext = FileContext.getFileContext(this.conf);
        }
        catch (UnsupportedFileSystemException ufe)
        {
            throw new RuntimeException("Error in instantiating YarnClient", ufe);
        }
    }

    @InterfaceAudience.Private
    public void setResourceMgrDelegate(ResourceMgrDelegate resMgrDelegate)
    {
        this.resMgrDelegate = resMgrDelegate;
    }

    public void cancelDelegationToken(org.apache.hadoop.security.token.Token<DelegationTokenIdentifier> arg0)
            throws IOException, InterruptedException
    {
        throw new UnsupportedOperationException("Use Token.renew instead");
    }

    public TaskTrackerInfo[] getActiveTrackers()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getActiveTrackers();
    }

    public JobStatus[] getAllJobs()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getAllJobs();
    }

    public TaskTrackerInfo[] getBlacklistedTrackers()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getBlacklistedTrackers();
    }

    public ClusterMetrics getClusterMetrics()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getClusterMetrics();
    }

    @VisibleForTesting
    void addHistoryToken(Credentials ts)
            throws IOException, InterruptedException
    {
        MRClientProtocol hsProxy = this.clientCache.getInitializedHSProxy();
        if ((UserGroupInformation.isSecurityEnabled()) && (hsProxy != null))
        {
            RMDelegationTokenSelector tokenSelector = new RMDelegationTokenSelector();
            Text service = this.resMgrDelegate.getRMDelegationTokenService();
            if (tokenSelector.selectToken(service, ts.getAllTokens()) != null)
            {
                Text hsService = SecurityUtil.buildTokenService(hsProxy.getConnectAddress());
                if (ts.getToken(hsService) == null) {
                    ts.addToken(hsService, getDelegationTokenFromHS(hsProxy));
                }
            }
        }
    }

    @VisibleForTesting
    org.apache.hadoop.security.token.Token<?> getDelegationTokenFromHS(MRClientProtocol hsProxy)
            throws IOException, InterruptedException
    {
        GetDelegationTokenRequest request = (GetDelegationTokenRequest)this.recordFactory.newRecordInstance(GetDelegationTokenRequest.class);

        request.setRenewer(Master.getMasterPrincipal(this.conf));

        org.apache.hadoop.yarn.api.records.Token mrDelegationToken = hsProxy.getDelegationToken(request).getDelegationToken();

        return ConverterUtils.convertFromYarn(mrDelegationToken, hsProxy.getConnectAddress());
    }

    public org.apache.hadoop.security.token.Token<DelegationTokenIdentifier> getDelegationToken(Text renewer)
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getDelegationToken(renewer);
    }

    public String getFilesystemName()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getFilesystemName();
    }

    public JobID getNewJobID()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getNewJobID();
    }

    public QueueInfo getQueue(String queueName)
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getQueue(queueName);
    }

    public QueueAclsInfo[] getQueueAclsForCurrentUser()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getQueueAclsForCurrentUser();
    }

    public QueueInfo[] getQueues()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getQueues();
    }

    public QueueInfo[] getRootQueues()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getRootQueues();
    }

    public QueueInfo[] getChildQueues(String parent)
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getChildQueues(parent);
    }

    public String getStagingAreaDir()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getStagingAreaDir();
    }

    public String getSystemDir()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getSystemDir();
    }

    public long getTaskTrackerExpiryInterval()
            throws IOException, InterruptedException
    {
        return this.resMgrDelegate.getTaskTrackerExpiryInterval();
    }

    public JobStatus submitJob(JobID jobId, String jobSubmitDir, Credentials ts)
            throws IOException, InterruptedException
    {
        addHistoryToken(ts);


        ApplicationSubmissionContext appContext = createApplicationSubmissionContext(this.conf, jobSubmitDir, ts);
        try
        {
            ApplicationId applicationId = this.resMgrDelegate.submitApplication(appContext);


            ApplicationReport appMaster = this.resMgrDelegate.getApplicationReport(applicationId);

            String diagnostics = appMaster == null ? "application report is null" : appMaster.getDiagnostics();
            if ((appMaster == null) || (appMaster.getYarnApplicationState() == YarnApplicationState.FAILED) || (appMaster.getYarnApplicationState() == YarnApplicationState.KILLED)) {
                throw new IOException("Failed to run job : " + diagnostics);
            }
            return this.clientCache.getClient(jobId).getJobStatus(jobId);
        }
        catch (YarnException e)
        {
            throw new IOException(e);
        }
    }

    private LocalResource createApplicationResource(FileContext fs, Path p, LocalResourceType type)
            throws IOException
    {
        LocalResource rsrc = (LocalResource)this.recordFactory.newRecordInstance(LocalResource.class);
        FileStatus rsrcStat = fs.getFileStatus(p);
        rsrc.setResource(ConverterUtils.getYarnUrlFromPath(fs.getDefaultFileSystem().resolvePath(rsrcStat.getPath())));

        rsrc.setSize(rsrcStat.getLen());
        rsrc.setTimestamp(rsrcStat.getModificationTime());
        rsrc.setType(type);
        rsrc.setVisibility(LocalResourceVisibility.APPLICATION);
        return rsrc;
    }

    public ApplicationSubmissionContext createApplicationSubmissionContext(Configuration jobConf, String jobSubmitDir, Credentials ts)
            throws IOException
    {
        ApplicationId applicationId = this.resMgrDelegate.getApplicationId();


        Resource capability = (Resource)this.recordFactory.newRecordInstance(Resource.class);
        capability.setMemory(this.conf.getInt("yarn.app.mapreduce.am.resource.mb", 1536));




        capability.setVirtualCores(this.conf.getInt("yarn.app.mapreduce.am.resource.cpu-vcores", 1));




        LOG.debug("AppMaster capability = " + capability);


        Map<String, LocalResource> localResources = new HashMap();


        Path jobConfPath = new Path(jobSubmitDir, "job.xml");

        URL yarnUrlForJobSubmitDir = ConverterUtils.getYarnUrlFromPath(this.defaultFileContext.getDefaultFileSystem().resolvePath(this.defaultFileContext.makeQualified(new Path(jobSubmitDir))));



        LOG.debug("Creating setup context, jobSubmitDir url is " + yarnUrlForJobSubmitDir);


        localResources.put("job.xml", createApplicationResource(this.defaultFileContext, jobConfPath, LocalResourceType.FILE));
        if (jobConf.get("mapreduce.job.jar") != null)
        {
            Path jobJarPath = new Path(jobConf.get("mapreduce.job.jar"));
            LocalResource rc = createApplicationResource(FileContext.getFileContext(jobJarPath.toUri(), jobConf), jobJarPath, LocalResourceType.PATTERN);



            String pattern = this.conf.getPattern("mapreduce.job.jar.unpack.pattern", JobConf.UNPACK_JAR_PATTERN_DEFAULT).pattern();

            rc.setPattern(pattern);
            localResources.put("job.jar", rc);
        }
        else
        {
            LOG.info("Job jar is not present. Not adding any jar to the list of resources.");
        }
        for (String s : new String[] { "job.split", "job.splitmetainfo" }) {
            localResources.put("jobSubmitDir/" + s, createApplicationResource(this.defaultFileContext, new Path(jobSubmitDir, s), LocalResourceType.FILE));
        }
        DataOutputBuffer dob = new DataOutputBuffer();
        ts.writeTokenStorageToStream(dob);
        ByteBuffer securityTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());


        //List<String> vargs = new ArrayList(8);
        //vargs.add(MRApps.crossPlatformifyMREnv(jobConf, ApplicationConstants.Environment.JAVA_HOME) + "/bin/java");

        List<String> vargs = new ArrayList<String>(8);
        vargs.add("$JAVA_HOME/bin/java");


        long logSize = jobConf.getLong("yarn.app.mapreduce.am.container.log.limit.kb", 0L) << 10;

        String logLevel = jobConf.get("yarn.app.mapreduce.am.log.level", "INFO");

        int numBackups = jobConf.getInt("yarn.app.mapreduce.am.container.log.backups", 0);

      //  MRApps.addLog4jSystemProperties(logLevel, logSize, numBackups, vargs, this.conf);


        warnForJavaLibPath(this.conf.get("mapreduce.map.java.opts", ""), "map", "mapreduce.map.java.opts", "mapreduce.map.env");

        warnForJavaLibPath(this.conf.get("mapreduce.admin.map.child.java.opts", ""), "map", "mapreduce.admin.map.child.java.opts", "mapreduce.admin.user.env");

        warnForJavaLibPath(this.conf.get("mapreduce.reduce.java.opts", ""), "reduce", "mapreduce.reduce.java.opts", "mapreduce.reduce.env");

        warnForJavaLibPath(this.conf.get("mapreduce.admin.reduce.child.java.opts", ""), "reduce", "mapreduce.admin.reduce.child.java.opts", "mapreduce.admin.user.env");




        String mrAppMasterAdminOptions = this.conf.get("yarn.app.mapreduce.am.admin-command-opts", "");

        warnForJavaLibPath(mrAppMasterAdminOptions, "app master", "yarn.app.mapreduce.am.admin-command-opts", "yarn.app.mapreduce.am.admin.user.env");

        vargs.add(mrAppMasterAdminOptions);


        String mrAppMasterUserOptions = this.conf.get("yarn.app.mapreduce.am.command-opts", "-Xmx1024m");

        warnForJavaLibPath(mrAppMasterUserOptions, "app master", "yarn.app.mapreduce.am.command-opts", "yarn.app.mapreduce.am.env");

        vargs.add(mrAppMasterUserOptions);
        if (jobConf.getBoolean("yarn.app.mapreduce.am.profile", false))
        {
            String profileParams = jobConf.get("yarn.app.mapreduce.am.profile.params", "-agentlib:hprof=cpu=samples,heap=sites,force=n,thread=y,verbose=n,file=%s");
            if (profileParams != null) {
                vargs.add(String.format(profileParams, new Object[] { "<LOG_DIR>/" + TaskLog.LogName.PROFILE }));
            }
        }
        vargs.add("org.apache.hadoop.mapreduce.v2.app.MRAppMaster");
        vargs.add("1><LOG_DIR>/stdout");

        vargs.add("2><LOG_DIR>/stderr");



        Vector<String> vargsFinal = new Vector(8);

        StringBuilder mergedCommand = new StringBuilder();
        for (CharSequence str : vargs) {
            mergedCommand.append(str).append(" ");
        }
        vargsFinal.add(mergedCommand.toString());

        LOG.debug("Command to launch container for ApplicationMaster is : " + mergedCommand);




        Map<String, String> environment = new HashMap();
        MRApps.setClasspath(environment, this.conf);
        replaceEnvironment(environment);

        environment.put(ApplicationConstants.Environment.SHELL.name(), this.conf.get("mapreduce.admin.user.shell", "/bin/bash"));




        MRApps.addToEnvironment(environment, ApplicationConstants.Environment.LD_LIBRARY_PATH.name(), MRApps.crossPlatformifyMREnv(this.conf, ApplicationConstants.Environment.PWD), this.conf);



        MRApps.setEnvFromInputString(environment, this.conf.get("yarn.app.mapreduce.am.admin.user.env"), this.conf);


        MRApps.setEnvFromInputString(environment, this.conf.get("yarn.app.mapreduce.am.env"), this.conf);



        MRApps.setupDistributedCache(jobConf, localResources);

        Map<ApplicationAccessType, String> acls = new HashMap(2);

        acls.put(ApplicationAccessType.VIEW_APP, jobConf.get("mapreduce.job.acl-view-job", " "));

        acls.put(ApplicationAccessType.MODIFY_APP, jobConf.get("mapreduce.job.acl-modify-job", " "));




        ContainerLaunchContext amContainer = ContainerLaunchContext.newInstance(localResources, environment, vargsFinal, null, securityTokens, acls);



        Collection<String> tagsFromConf = jobConf.getTrimmedStringCollection("mapreduce.job.tags");



        ApplicationSubmissionContext appContext = (ApplicationSubmissionContext)this.recordFactory.newRecordInstance(ApplicationSubmissionContext.class);

        appContext.setApplicationId(applicationId);
        appContext.setQueue(jobConf.get("mapreduce.job.queuename", "default"));



        ReservationId reservationID = null;
        try
        {
            reservationID = ReservationId.parseReservationId(jobConf.get("mapreduce.job.reservation.id"));
        }
        catch (NumberFormatException e)
        {
            String errMsg = "Invalid reservationId: " + jobConf.get("mapreduce.job.reservation.id") + " specified for the app: " + applicationId;


            LOG.warn(errMsg);
            throw new IOException(errMsg);
        }
        if (reservationID != null)
        {
            appContext.setReservationID(reservationID);
            LOG.info("SUBMITTING ApplicationSubmissionContext app:" + applicationId + " to queue:" + appContext.getQueue() + " with reservationId:" + appContext.getReservationID());
        }
        appContext.setApplicationName(jobConf.get("mapreduce.job.name", "N/A"));


        appContext.setCancelTokensWhenComplete(this.conf.getBoolean("mapreduce.job.complete.cancel.delegation.tokens", true));

        appContext.setAMContainerSpec(amContainer);
        appContext.setMaxAppAttempts(this.conf.getInt("mapreduce.am.max-attempts", 2));


        appContext.setResource(capability);
        appContext.setApplicationType("MAPREDUCE");
        if ((tagsFromConf != null) && (!tagsFromConf.isEmpty())) {
            appContext.setApplicationTags(new HashSet(tagsFromConf));
        }
        return appContext;
    }


    private void replaceEnvironment(Map<String, String> environment) {
        String tmpClassPath = environment.get("CLASSPATH");
        tmpClassPath=tmpClassPath.replaceAll(";", ":");
        tmpClassPath=tmpClassPath.replaceAll("%PWD%", "\\$PWD");
        tmpClassPath=tmpClassPath.replaceAll("%HADOOP_MAPRED_HOME%", "\\$HADOOP_MAPRED_HOME");
        tmpClassPath= tmpClassPath.replaceAll("\\\\", "/" );
        environment.put("CLASSPATH",tmpClassPath);
    }

    public void setJobPriority(JobID arg0, String arg1)
            throws IOException, InterruptedException
    {
        this.resMgrDelegate.setJobPriority(arg0, arg1);
    }


    public long getProtocolVersion(String arg0, long arg1)
            throws IOException
    {
        return this.resMgrDelegate.getProtocolVersion(arg0, arg1);
    }

    public long renewDelegationToken(org.apache.hadoop.security.token.Token<DelegationTokenIdentifier> arg0)
            throws IOException, InterruptedException
    {
        throw new UnsupportedOperationException("Use Token.renew instead");
    }

    public Counters getJobCounters(JobID arg0)
            throws IOException, InterruptedException
    {
        return this.clientCache.getClient(arg0).getJobCounters(arg0);
    }

    public String getJobHistoryDir()
            throws IOException, InterruptedException
    {
        return JobHistoryUtils.getConfiguredHistoryServerDoneDirPrefix(this.conf);
    }

    public JobStatus getJobStatus(JobID jobID)
            throws IOException, InterruptedException
    {
        JobStatus status = this.clientCache.getClient(jobID).getJobStatus(jobID);
        return status;
    }

    public TaskCompletionEvent[] getTaskCompletionEvents(JobID arg0, int arg1, int arg2)
            throws IOException, InterruptedException
    {
        return this.clientCache.getClient(arg0).getTaskCompletionEvents(arg0, arg1, arg2);
    }

    public String[] getTaskDiagnostics(TaskAttemptID arg0)
            throws IOException, InterruptedException
    {
        return this.clientCache.getClient(arg0.getJobID()).getTaskDiagnostics(arg0);
    }

    public TaskReport[] getTaskReports(JobID jobID, TaskType taskType)
            throws IOException, InterruptedException
    {
        return this.clientCache.getClient(jobID).getTaskReports(jobID, taskType);
    }

    private void killUnFinishedApplication(ApplicationId appId)
            throws IOException
    {
        ApplicationReport application = null;
        try
        {
            application = this.resMgrDelegate.getApplicationReport(appId);
        }
        catch (YarnException e)
        {
            throw new IOException(e);
        }
        if ((application.getYarnApplicationState() == YarnApplicationState.FINISHED) || (application.getYarnApplicationState() == YarnApplicationState.FAILED) || (application.getYarnApplicationState() == YarnApplicationState.KILLED)) {
            return;
        }
        killApplication(appId);
    }

    private void killApplication(ApplicationId appId)
            throws IOException
    {
        try
        {
            this.resMgrDelegate.killApplication(appId);
        }
        catch (YarnException e)
        {
            throw new IOException(e);
        }
    }

    private boolean isJobInTerminalState(JobStatus status)
    {
        return (status.getState() == JobStatus.State.KILLED) || (status.getState() == JobStatus.State.FAILED) || (status.getState() == JobStatus.State.SUCCEEDED);
    }

    public void killJob(JobID arg0)
            throws IOException, InterruptedException
    {
        JobStatus status = this.clientCache.getClient(arg0).getJobStatus(arg0);
        ApplicationId appId = TypeConverter.toYarn(arg0).getAppId();
        if (status == null)
        {
            killUnFinishedApplication(appId);
            return;
        }
        if (status.getState() != JobStatus.State.RUNNING)
        {
            killApplication(appId);
            return;
        }
        try
        {
            this.clientCache.getClient(arg0).killJob(arg0);
            long currentTimeMillis = System.currentTimeMillis();
            long timeKillIssued = currentTimeMillis;
            while ((currentTimeMillis < timeKillIssued + 10000L) && (!isJobInTerminalState(status)))
            {
                try
                {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException ie)
                {
                    break;
                }
                currentTimeMillis = System.currentTimeMillis();
                status = this.clientCache.getClient(arg0).getJobStatus(arg0);
                if (status == null)
                {
                    killUnFinishedApplication(appId);
                    return;
                }
            }
        }
        catch (IOException io)
        {
            LOG.debug("Error when checking for application status", io);
        }
        if ((status != null) && (!isJobInTerminalState(status))) {
            killApplication(appId);
        }
    }

    public boolean killTask(TaskAttemptID arg0, boolean arg1)
            throws IOException, InterruptedException
    {
        return this.clientCache.getClient(arg0.getJobID()).killTask(arg0, arg1);
    }

    public AccessControlList getQueueAdmins(String arg0)
            throws IOException
    {
        return new AccessControlList("*");
    }

    public Cluster.JobTrackerStatus getJobTrackerStatus()
            throws IOException, InterruptedException
    {
        return Cluster.JobTrackerStatus.RUNNING;
    }

    public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int clientMethodsHash)
            throws IOException
    {
        return ProtocolSignature.getProtocolSignature(this, protocol, clientVersion, clientMethodsHash);
    }

    public LogParams getLogFileParams(JobID jobID, TaskAttemptID taskAttemptID)
            throws IOException
    {
        return this.clientCache.getClient(jobID).getLogFilePath(jobID, taskAttemptID);
    }

    private static void warnForJavaLibPath(String opts, String component, String javaConf, String envConf)
    {
        if ((opts != null) && (opts.contains("-Djava.library.path"))) {
            LOG.warn("Usage of -Djava.library.path in " + javaConf + " can cause " + "programs to no longer function if hadoop native libraries " + "are used. These values should be set as part of the " + "LD_LIBRARY_PATH in the " + component + " JVM env using " + envConf + " config settings.");
        }
    }






}
