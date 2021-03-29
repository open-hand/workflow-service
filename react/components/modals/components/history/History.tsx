import React from 'react';
import { observer } from 'mobx-react-lite';
import ApproveHistory from '@/components/history';
import store from '@/components/modals/store';

import './History.less';

const prefix = 'c7n-backlogApprove-approveHistory';

const History = () => {
  const { historyList } = store;
  return (
    <div className={prefix}>
      <ApproveHistory historyList={historyList} />
    </div>
  );
};

export default observer(History);
