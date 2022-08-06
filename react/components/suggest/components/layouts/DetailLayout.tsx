import { Button, DataSet, Form, Select, TextArea, } from 'choerodon-ui/pro';
import { ButtonColor, FuncType } from 'choerodon-ui/pro/lib/button/enum';
import { ResizeType } from 'choerodon-ui/pro/lib/text-area/enum';
import React, { useCallback } from 'react';
import { Upload } from 'choerodon-ui';
import SingleFileUpload from '@choerodon/agile/lib/components/SingleFileUpload';
import validateFile from '@choerodon/agile/lib/utils/File';
import Section from '@/components/section';
import { IProcessAttachment } from '@/common/types';
import { observer } from 'mobx-react-lite';
import CommentTemplateManage from '../comment-template-manage';
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
    uploadProcessAttachment,
    processAttachments,
    deleteProcessAttachment,
  } = props;
  const handleFileUpload = useCallback((option) => {
    if (validateFile([option.file])) {
      uploadProcessAttachment(option.file);
    }
  }, [uploadProcessAttachment]);
  const handleDeleteFile = useCallback((item: IProcessAttachment) => {
    deleteProcessAttachment(item.fileUrl);
  }, [deleteProcessAttachment]);
  return (
    <Section
      title="审批意见"
      style={{
        marginTop: 0,
        marginBottom: -20,
      }}
      buttons={(
        <>
          <Upload customRequest={handleFileUpload} showUploadList={false}>
            <Button icon="file_upload" color={'primary' as ButtonColor} />
          </Upload>
          <Button
            icon="settings-o"
            color={'primary' as ButtonColor}
            style={{ marginLeft: 7, marginRight: 5 }}
            onClick={onManageClick}
          />
        </>
      )}
    >
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
        </div>
        <TextArea name="commentContent" resize={'vertical' as ResizeType} rows={1} />
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Button
            funcType={'raised' as FuncType}
            onClick={onSaveCommentTemplateClick}
          >
            收藏审批意见
          </Button>
        </div>
      </Form>
      {
        processAttachments && processAttachments.length > 0 && (
          <div style={{ display: 'flex', flexWrap: 'wrap', marginBottom: 10 }}>
            {processAttachments.map((item) => (
              <SingleFileUpload
                key={item.fileUrl}
                url={item.fileUrl}
                fileName={item.fileName}
                hasDeletePermission
                percent={0}
                onDeleteFile={() => {
                  handleDeleteFile(item);
                }}
              />
            ))}
          </div>
        )
      }
      <CommentTemplateManage
        data={data}
        manageVisible={manageVisible}
        setManageVisible={setManageVisible}
        onSaveCommentTemplate={onSaveCommentTemplate}
        onDeleteCommentTemplate={onDeleteCommentTemplate}
      />
    </Section>
  );
};

export default observer(SuggestLayout);
