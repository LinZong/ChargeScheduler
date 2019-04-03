package nemesiss.scheduler.change.chargescheduler.Utils;

public interface TaskPostExecuteWrapper<TTaskReturn>
{
     void DoOnPostExecute(TTaskReturn TaskRet);
}
