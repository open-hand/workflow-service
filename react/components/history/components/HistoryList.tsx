import React from 'react';
import { observer } from 'mobx-react-lite';
import UserHead from '@choerodon/agile/lib/components/UserHead';
import { ProcessHistory } from '@/common/types';
import { reverse } from 'lodash';
import HistoryItem from './HistoryItem';
import './HistoryList.less';

const prefix = 'c7n-backlogApprove-historyList';

const HistoryList: React.FC<{
  expand: boolean,
  historyList: ProcessHistory[],
}> = ({ expand, historyList }) => (
  <div className={`${prefix}`}>
    {
      (historyList || []).map((log: ProcessHistory, i: number, arr: ProcessHistory[]) => ((i >= 4 && expand) || i < 5) && (
        <>
          {
            log.runTaskHistory?.nodeType === 'subProcessNode' && (log.runTaskHistory?.subProcessHistoryList || []).length > 0 && (
              <>
                {
                  reverse(log.runTaskHistory?.subProcessHistoryList || []).map((sublog) => (
                    <div key={sublog.taskHistoryId} className={`${prefix}-log`}>
                      <div
                        className={`${prefix}-log-user`}
                      />
                      <div className={`${prefix}-log-right`}>
                        <div className={`${prefix}-log-logOperation`}>
                          <HistoryItem log={sublog} isSubProcess />
                        </div>
                        <div className={`${prefix}-log-lastUpdateDate`}>
                          {sublog.endDate || ''}
                        </div>
                      </div>
                    </div>
                  ))
                }
              </>
            )
          }
          <div key={log.runTaskHistory?.taskHistoryId} className={`${prefix}-log`}>
            <div
              className={`${prefix}-log-user`}
            >
              {
                i && log.userDTO && (log.userDTO?.id === arr[i - 1].userDTO?.id) ? null : (
                  <>
                    {
                      log.runTaskHistory?.nodeType === 'startNode' || log.runTaskHistory?.nodeType === 'endNode' ? (
                        <div className={`${prefix}-log-user-startOrEnd`}>
                          {
                            log.runTaskHistory?.nodeType === 'startNode' ? '开始' : '结束'
                          }
                        </div>
                      ) : (
                        <UserHead
                          user={log.userDTO}
                          hiddenText
                          type="datalog"
                        />
                      )
                    }
                  </>
                )
            }
            </div>
            <div className={`${prefix}-log-right`}>
              <div className={`${prefix}-log-logOperation`}>
                <HistoryItem log={log.runTaskHistory} />
              </div>
              <div className={`${prefix}-log-lastUpdateDate`}>
                {log.runTaskHistory?.endDate || ''}
              </div>
            </div>
          </div>

        </ >
      ))
      }
  </div>
);

export default observer(HistoryList);
