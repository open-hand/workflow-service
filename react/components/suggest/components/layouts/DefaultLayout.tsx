import { Button, DataSet, Form, Modal, Select, TextArea, } from 'choerodon-ui/pro';
import { ResizeType } from 'choerodon-ui/pro/lib/text-area/enum';
import React, { useCallback, useEffect, useMemo, useRef, } from 'react';
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
      okText: '关闭',
      // @ts-ignore
      footer: (okBtn, cancelButton) => okBtn,
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
      <div style={{ display: 'flex', alignItems: 'center' }}>
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
          icon="save-o"
          onClick={onSaveCommentTemplateClick}
          style={{
            marginLeft: 14,
          }}
        >
          收藏审批意见
        </Button>
        <Button
          icon="settings-o"
          onClick={handleManageClick}
        >
          审批意见管理
        </Button>
      </div>
      <TextArea name="commentContent" resize={'vertical' as ResizeType} />
      <div style={{ marginTop: -10 }}>
        <ProcessAttachment {...otherProps} />
      </div>
    </Form>
  );
};

export default observer(SuggestLayout);
