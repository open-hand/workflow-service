import React from 'react';
import { Button } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';

import { CustomValidator } from 'choerodon-ui/pro/lib/validator/Validator';
import { stores } from '@choerodon/boot';
import ManageItem from './ManageItem';
import './ManageList.less';

const { HeaderStore } = stores;
export interface ManageListProps {
  fixed?: boolean
  title: string
  visible: boolean,
  setVisible: (visible: boolean) => void,
  data: Record<string, any>[]
  onDelete: (data: Object) => void
  onSubmit: (data: Record<string, any>, newValue: string) => void
  validator?: CustomValidator
  textField: string
  valueField: string
}
const ManageList: React.FC<ManageListProps> = ({
  visible,
  setVisible,
  data,
  onDelete,
  onSubmit,
  validator,
  textField,
  valueField,
  title,
  fixed = true,
}) => {
  if (!visible) {
    return null;
  }
  const renderContent = () => (
    <>
      {
        data && data.length > 0 ? (
          <ul className="c7n-ManageList-content">
            {
              data.map((item) => (
                <ManageItem
                  key={item[valueField]}
                  data={item}
                  onSubmit={onSubmit}
                  onDelete={onDelete}
                  validator={validator}
                  textField={textField}
                />
              ))
            }
          </ul>
        ) : <div style={{ textAlign: 'center', marginTop: 15, color: 'var(--text-color3)' }}>暂无数据</div>
      }
    </>
  );
  if (!fixed) {
    return renderContent();
  }
  return (
    <div
      className="c7n-ManageList"
      style={{ width: 350, top: HeaderStore.announcementClosed ? 50 : 100 }}
    >
      <div className="c7n-ManageList-header">
        <span>{title}</span>
        <Button
          shape="circle"
          icon="close"
          onClick={() => {
            setVisible(false);
          }}
        />
      </div>
      {renderContent()}
    </div>
  );
};
export default observer(ManageList);
