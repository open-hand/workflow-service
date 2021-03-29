import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import { Button } from 'choerodon-ui/pro';
import { Spin } from 'choerodon-ui';
import { ProcessHistory } from '@/common/types';
import HistoryList from './components/HistoryList';

import './History.less';

const prefix = 'c7n-approveHistory';

const History: React.FC<{
  historyList: ProcessHistory[]
  isLoading?: boolean
}> = ({ historyList = [], isLoading = false }) => {
  const [expand, setExpand] = useState<boolean>(false);

  const handleExpandBtnClick = () => {
    setExpand(!expand);
  };

  return (
    <div className={prefix}>
      <Spin spinning={isLoading}>
        {
          (isLoading || historyList.length > 0) ? (
            <HistoryList
              expand={expand}
              historyList={historyList}
            />
          ) : (
            <div style={{
              textAlign: 'center',
              color: 'rgba(0,0,0,0.65)',
            }}
            >
              暂无记录
            </div>
          )
        }
        {
        historyList.length > 5 && (
          <Button
            className={`${prefix}-expandBtn`}
            onClick={handleExpandBtnClick}
            icon={expand ? 'baseline-arrow_drop_up icon' : 'baseline-arrow_right icon'}
          >
            {
              expand ? '折叠' : '展开'
            }
          </Button>
        )
      }
      </Spin>

    </div>
  );
};

export default observer(History);
