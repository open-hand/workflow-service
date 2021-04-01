import {
  DataSet, Form, TextArea, Select, Button, Modal,
} from 'choerodon-ui/pro';
import { ButtonColor, FuncType } from 'choerodon-ui/pro/lib/button/enum';
import { ResizeType } from 'choerodon-ui/pro/lib/text-area/enum';
import React, {
  useCallback, useEffect, useMemo, useRef,
} from 'react';
import { observer } from 'mobx-react-lite';
import MODAL_WIDTH from '@choerodon/agile/lib/constants/MODAL_WIDTH';
import CommentTemplateManage from '../comment-template-manage';
import ProcessAttachment from '../process-attachment';
import { SuggestProps } from '../..';
import styles from './index.less';

export type SuggestLayoutProps = SuggestProps & {
  onManageClick: () => void
  onSaveCommentTemplateClick: () => void
  dataSet: DataSet
}
const { Option } = Select;
const SuggestLayout: React.FC<SuggestLayoutProps> = (props) => {
  const {
    data,
    manageVisible,
    setManageVisible,
    onSaveCommentTemplate,
    onDeleteCommentTemplate,
    onManageClick,
    onSaveCommentTemplateClick,
    dataSet,
    ...otherProps
  } = props;
  const modalRef = useRef<any>();
  const ModalProps = useMemo(() => ({
    fixed: false,
    data,
    manageVisible: true,
    setManageVisible,
    onSaveCommentTemplate,
    onDeleteCommentTemplate,
  }), [data, onDeleteCommentTemplate, onSaveCommentTemplate, setManageVisible]);
  useEffect(() => {
    if (modalRef.current) {
      modalRef.current.update({
        children: <CommentTemplateManage
          {...ModalProps}
        />,
      });
    }
  }, [ModalProps]);
  const handleManageClick = useCallback(() => {
    const modal = Modal.open({
      className: styles.modal,
      key: 'ManageListModal',
      title: '审批意见管理',
      drawer: true,
      style: {
        width: MODAL_WIDTH.small,
      },
      cancelText: '关闭',
      // @ts-ignore
      footer: (_, cancelButton) => cancelButton,
      onCancel: () => {
        modalRef.current = undefined;
      },
      children: <CommentTemplateManage
        {...ModalProps}
      />,
    });
    modalRef.current = modal;
  }, [ModalProps]);
  return (
    <Form dataSet={dataSet} className={styles.form} style={{ width: '100%' }}>
      <div style={{ display: 'flex' }}>
        <div style={{ flex: 1 }}>
          <Select name="commentTemplates" style={{ width: '100%' }}>
            {data.map((t) => (
              <Option value={t.commentTemplateId}>
                {t.commentContent}
              </Option>
            ))}
          </Select>
        </div>
        <Button
          icon="settings"
          color={'blue' as ButtonColor}
          style={{ marginLeft: 20 }}
          onClick={handleManageClick}
        >
          审批意见管理
        </Button>
      </div>
      <TextArea name="commentContent" resize={'vertical' as ResizeType} />
      <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
        <Button
          color={'blue' as ButtonColor}
          funcType={'raised' as FuncType}
          onClick={onSaveCommentTemplateClick}
        >
          保留审批意见
        </Button>
        <Button color={'blue' as ButtonColor}>取消</Button>
      </div>
      <ProcessAttachment {...otherProps} />
    </Form>
  );
};

export default observer(SuggestLayout);
