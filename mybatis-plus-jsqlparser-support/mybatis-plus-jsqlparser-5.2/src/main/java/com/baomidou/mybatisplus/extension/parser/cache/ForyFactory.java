/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.extension.parser.cache;

import org.apache.fory.Fory;
import org.apache.fory.ThreadSafeFory;

/**
 * Fory Factory.
 *
 * @author laokou
 */
public final class ForyFactory {

    private static final ForyFactory FACTORY = new ForyFactory();

    private final ThreadSafeFory FORY = Fory.builder()
            // Fory 1.x 默认跨语言模式,要求引用字段非空;JSqlParser AST 大量字段可为 null,需使用纯 Java 模式
            .withXlang(false)
            // 开启异步编译
            .withAsyncCompilation(true).buildThreadSafeFory();

    public ForyFactory() {
        FORY.register(net.sf.jsqlparser.expression.Alias.class);
        FORY.register(net.sf.jsqlparser.expression.Alias.AliasColumn.class);
        FORY.register(net.sf.jsqlparser.expression.AllValue.class);
        FORY.register(net.sf.jsqlparser.expression.AnalyticExpression.class);
        FORY.register(net.sf.jsqlparser.expression.AnalyticType.class);
        FORY.register(net.sf.jsqlparser.expression.AnyComparisonExpression.class);
        FORY.register(net.sf.jsqlparser.expression.AnyType.class);
        FORY.register(net.sf.jsqlparser.expression.ArrayConstructor.class);
        FORY.register(net.sf.jsqlparser.expression.ArrayExpression.class);
        FORY.register(net.sf.jsqlparser.expression.BinaryExpression.class);
        FORY.register(net.sf.jsqlparser.expression.BooleanValue.class);
        FORY.register(net.sf.jsqlparser.expression.CaseExpression.class);
        FORY.register(net.sf.jsqlparser.expression.CastExpression.class);
        FORY.register(net.sf.jsqlparser.expression.CastExpression.DataType.class);
        FORY.register(net.sf.jsqlparser.expression.CollateExpression.class);
        FORY.register(net.sf.jsqlparser.expression.ConnectByPriorOperator.class);
        FORY.register(net.sf.jsqlparser.expression.ConnectByRootOperator.class);
        FORY.register(net.sf.jsqlparser.expression.DateTimeLiteralExpression.class);
        FORY.register(net.sf.jsqlparser.expression.DateTimeLiteralExpression.DateTime.class);
        FORY.register(net.sf.jsqlparser.expression.DateValue.class);
        FORY.register(net.sf.jsqlparser.expression.DoubleValue.class);
        FORY.register(net.sf.jsqlparser.expression.ExtractExpression.class);
        FORY.register(net.sf.jsqlparser.expression.FilterOverImpl.class);
        FORY.register(net.sf.jsqlparser.expression.Function.class);
        FORY.register(net.sf.jsqlparser.expression.Function.HavingClause.class);
        FORY.register(net.sf.jsqlparser.expression.Function.NullHandling.class);
        FORY.register(net.sf.jsqlparser.expression.HexValue.class);
        FORY.register(net.sf.jsqlparser.expression.HighExpression.class);
        FORY.register(net.sf.jsqlparser.expression.IntervalExpression.class);
        FORY.register(net.sf.jsqlparser.expression.Inverse.class);
        FORY.register(net.sf.jsqlparser.expression.JdbcNamedParameter.class);
        FORY.register(net.sf.jsqlparser.expression.JdbcParameter.class);
        FORY.register(net.sf.jsqlparser.expression.JsonAggregateFunction.class);
        FORY.register(net.sf.jsqlparser.expression.JsonAggregateOnNullType.class);
        FORY.register(net.sf.jsqlparser.expression.JsonAggregateUniqueKeysType.class);
        FORY.register(net.sf.jsqlparser.expression.JsonExpression.class);
        FORY.register(net.sf.jsqlparser.expression.JsonFunction.class);
        FORY.register(net.sf.jsqlparser.expression.JsonFunctionExpression.class);
        FORY.register(net.sf.jsqlparser.expression.JsonFunctionType.class);
        FORY.register(net.sf.jsqlparser.expression.JsonKeyValuePair.class);
        FORY.register(net.sf.jsqlparser.expression.KeepExpression.class);
        FORY.register(net.sf.jsqlparser.expression.LambdaExpression.class);
        FORY.register(net.sf.jsqlparser.expression.LongValue.class);
        FORY.register(net.sf.jsqlparser.expression.LowExpression.class);
        FORY.register(net.sf.jsqlparser.expression.MySQLGroupConcat.class);
        FORY.register(net.sf.jsqlparser.expression.MySQLIndexHint.class);
        FORY.register(net.sf.jsqlparser.expression.NextValExpression.class);
        FORY.register(net.sf.jsqlparser.expression.NotExpression.class);
        FORY.register(net.sf.jsqlparser.expression.NullValue.class);
        FORY.register(net.sf.jsqlparser.expression.NumericBind.class);
        FORY.register(net.sf.jsqlparser.expression.OracleHierarchicalExpression.class);
        FORY.register(net.sf.jsqlparser.expression.OracleHint.class);
        FORY.register(net.sf.jsqlparser.expression.OracleNamedFunctionParameter.class);
        FORY.register(net.sf.jsqlparser.expression.OrderByClause.class);
        FORY.register(net.sf.jsqlparser.expression.OverlapsCondition.class);
        FORY.register(net.sf.jsqlparser.expression.Parenthesis.class);
        FORY.register(net.sf.jsqlparser.expression.PartitionByClause.class);
        FORY.register(net.sf.jsqlparser.expression.PreferringClause.class);
        FORY.register(net.sf.jsqlparser.expression.RangeExpression.class);
        FORY.register(net.sf.jsqlparser.expression.RowConstructor.class);
        FORY.register(net.sf.jsqlparser.expression.RowGetExpression.class);
        FORY.register(net.sf.jsqlparser.expression.SQLServerHints.class);
        FORY.register(net.sf.jsqlparser.expression.SignedExpression.class);
        FORY.register(net.sf.jsqlparser.expression.SpannerInterleaveIn.OnDelete.class);
        FORY.register(net.sf.jsqlparser.expression.StringValue.class);
        FORY.register(net.sf.jsqlparser.expression.StructType.class);
        FORY.register(net.sf.jsqlparser.expression.StructType.Dialect.class);
        FORY.register(net.sf.jsqlparser.expression.TimeKeyExpression.class);
        FORY.register(net.sf.jsqlparser.expression.TimeValue.class);
        FORY.register(net.sf.jsqlparser.expression.TimestampValue.class);
        FORY.register(net.sf.jsqlparser.expression.TimezoneExpression.class);
        FORY.register(net.sf.jsqlparser.expression.TranscodingFunction.class);
        FORY.register(net.sf.jsqlparser.expression.TrimFunction.class);
        FORY.register(net.sf.jsqlparser.expression.TrimFunction.TrimSpecification.class);
        FORY.register(net.sf.jsqlparser.expression.UserVariable.class);
        FORY.register(net.sf.jsqlparser.expression.VariableAssignment.class);
        FORY.register(net.sf.jsqlparser.expression.WhenClause.class);
        FORY.register(net.sf.jsqlparser.expression.WindowDefinition.class);
        FORY.register(net.sf.jsqlparser.expression.WindowElement.class);
        FORY.register(net.sf.jsqlparser.expression.WindowElement.Type.class);
        FORY.register(net.sf.jsqlparser.expression.WindowOffset.class);
        FORY.register(net.sf.jsqlparser.expression.WindowOffset.Type.class);
        FORY.register(net.sf.jsqlparser.expression.WindowRange.class);
        FORY.register(net.sf.jsqlparser.expression.XMLSerializeExpr.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.Addition.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseLeftShift.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseRightShift.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.Concat.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.Division.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.IntegerDivision.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.Modulo.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.Multiplication.class);
        FORY.register(net.sf.jsqlparser.expression.operators.arithmetic.Subtraction.class);
        FORY.register(net.sf.jsqlparser.expression.operators.conditional.AndExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.conditional.OrExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.conditional.XorExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.Between.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.ComparisonOperator.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.ContainedBy.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.Contains.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.CosineSimilarity.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.DoubleAnd.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.EqualsTo.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.ExcludesExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.ExistsExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.ExpressionList.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.FullTextSearch.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.GeometryDistance.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.GreaterThan.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.InExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.IncludesExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.IsBooleanExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.IsDistinctExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.IsNullExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.IsUnknownExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.JsonOperator.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.LikeExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.LikeExpression.KeyWord.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.Matches.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.MemberOfExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.MinorThan.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.MinorThanEquals.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.NamedExpressionList.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.NotEqualsTo.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.OldOracleJoinBinaryExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.Plus.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.PriorTo.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperatorType.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.SimilarToExpression.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.TSQLLeftJoin.class);
        FORY.register(net.sf.jsqlparser.expression.operators.relational.TSQLRightJoin.class);
        FORY.register(net.sf.jsqlparser.parser.ASTNodeAccessImpl.class);
        FORY.register(net.sf.jsqlparser.parser.Token.class);
        FORY.register(net.sf.jsqlparser.parser.feature.Feature.class);
        FORY.register(net.sf.jsqlparser.schema.Column.class);
        FORY.register(net.sf.jsqlparser.schema.Sequence.class);
        FORY.register(net.sf.jsqlparser.schema.Sequence.ParameterType.class);
        FORY.register(net.sf.jsqlparser.schema.Synonym.class);
        FORY.register(net.sf.jsqlparser.schema.Table.class);
        FORY.register(net.sf.jsqlparser.statement.Block.class);
        FORY.register(net.sf.jsqlparser.statement.Commit.class);
        FORY.register(net.sf.jsqlparser.statement.CreateFunctionalStatement.class);
        FORY.register(net.sf.jsqlparser.statement.DeclareStatement.class);
        FORY.register(net.sf.jsqlparser.statement.DeclareStatement.TypeDefExpr.class);
        FORY.register(net.sf.jsqlparser.statement.DeclareType.class);
        FORY.register(net.sf.jsqlparser.statement.DescribeStatement.class);
        FORY.register(net.sf.jsqlparser.statement.ExplainStatement.class);
        FORY.register(net.sf.jsqlparser.statement.ExplainStatement.Option.class);
        FORY.register(net.sf.jsqlparser.statement.ExplainStatement.OptionType.class);
        FORY.register(net.sf.jsqlparser.statement.IfElseStatement.class);
        FORY.register(net.sf.jsqlparser.statement.OutputClause.class);
        FORY.register(net.sf.jsqlparser.statement.PurgeObjectType.class);
        FORY.register(net.sf.jsqlparser.statement.PurgeStatement.class);
        FORY.register(net.sf.jsqlparser.statement.ReferentialAction.class);
        FORY.register(net.sf.jsqlparser.statement.ReferentialAction.Action.class);
        FORY.register(net.sf.jsqlparser.statement.ReferentialAction.Type.class);
        FORY.register(net.sf.jsqlparser.statement.ResetStatement.class);
        FORY.register(net.sf.jsqlparser.statement.ReturningClause.Keyword.class);
        FORY.register(net.sf.jsqlparser.statement.RollbackStatement.class);
        FORY.register(net.sf.jsqlparser.statement.SavepointStatement.class);
        FORY.register(net.sf.jsqlparser.statement.SetStatement.class);
        FORY.register(net.sf.jsqlparser.statement.ShowColumnsStatement.class);
        FORY.register(net.sf.jsqlparser.statement.ShowStatement.class);
        FORY.register(net.sf.jsqlparser.statement.Statements.class);
        FORY.register(net.sf.jsqlparser.statement.UnsupportedStatement.class);
        FORY.register(net.sf.jsqlparser.statement.UseStatement.class);
        FORY.register(net.sf.jsqlparser.statement.alter.Alter.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterExpression.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterExpression.ColumnDataType.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterExpression.ColumnDropDefault.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterExpression.ColumnDropNotNull.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterExpression.ColumnSetDefault.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterExpression.ColumnSetVisibility.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterExpression.ConvertType.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterOperation.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterSession.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterSessionOperation.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterSystemOperation.class);
        FORY.register(net.sf.jsqlparser.statement.alter.AlterSystemStatement.class);
        FORY.register(net.sf.jsqlparser.statement.alter.RenameTableStatement.class);
        FORY.register(net.sf.jsqlparser.statement.alter.sequence.AlterSequence.class);
        FORY.register(net.sf.jsqlparser.statement.analyze.Analyze.class);
        FORY.register(net.sf.jsqlparser.statement.comment.Comment.class);
        FORY.register(net.sf.jsqlparser.statement.create.function.CreateFunction.class);
        FORY.register(net.sf.jsqlparser.statement.create.index.CreateIndex.class);
        FORY.register(net.sf.jsqlparser.statement.create.procedure.CreateProcedure.class);
        FORY.register(net.sf.jsqlparser.statement.create.schema.CreateSchema.class);
        FORY.register(net.sf.jsqlparser.statement.create.sequence.CreateSequence.class);
        FORY.register(net.sf.jsqlparser.statement.create.synonym.CreateSynonym.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.CheckConstraint.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.ColDataType.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.ColumnDefinition.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.CreateTable.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.ExcludeConstraint.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.ForeignKeyIndex.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.Index.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.Index.ColumnParams.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.NamedConstraint.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.PartitionDefinition.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.RowMovement.class);
        FORY.register(net.sf.jsqlparser.statement.create.table.RowMovementMode.class);
        FORY.register(net.sf.jsqlparser.statement.create.view.AlterView.class);
        FORY.register(net.sf.jsqlparser.statement.create.view.AutoRefreshOption.class);
        FORY.register(net.sf.jsqlparser.statement.create.view.CreateView.class);
        FORY.register(net.sf.jsqlparser.statement.create.view.ForceOption.class);
        FORY.register(net.sf.jsqlparser.statement.create.view.TemporaryOption.class);
        FORY.register(net.sf.jsqlparser.statement.delete.Delete.class);
        FORY.register(net.sf.jsqlparser.statement.delete.DeleteModifierPriority.class);
        FORY.register(net.sf.jsqlparser.statement.delete.ParenthesedDelete.class);
        FORY.register(net.sf.jsqlparser.statement.drop.Drop.class);
        FORY.register(net.sf.jsqlparser.statement.execute.Execute.class);
        FORY.register(net.sf.jsqlparser.statement.execute.Execute.ExecType.class);
        FORY.register(net.sf.jsqlparser.statement.grant.Grant.class);
        FORY.register(net.sf.jsqlparser.statement.insert.ConflictActionType.class);
        FORY.register(net.sf.jsqlparser.statement.insert.Insert.class);
        FORY.register(net.sf.jsqlparser.statement.insert.InsertConflictAction.class);
        FORY.register(net.sf.jsqlparser.statement.insert.InsertConflictTarget.class);
        FORY.register(net.sf.jsqlparser.statement.insert.InsertModifierPriority.class);
        FORY.register(net.sf.jsqlparser.statement.insert.ParenthesedInsert.class);
        FORY.register(net.sf.jsqlparser.statement.merge.Merge.class);
        FORY.register(net.sf.jsqlparser.statement.merge.MergeDelete.class);
        FORY.register(net.sf.jsqlparser.statement.merge.MergeInsert.class);
        FORY.register(net.sf.jsqlparser.statement.merge.MergeUpdate.class);
        FORY.register(net.sf.jsqlparser.statement.piped.AggregatePipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.AsPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.CallPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.DropPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.ExtendPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.FromQuery.class);
        FORY.register(net.sf.jsqlparser.statement.piped.JoinPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.LimitPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.OrderByPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.PipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.PivotPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.RenamePipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.SelectPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.SetOperationPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.SetPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.TableSamplePipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.UnPivotPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.WherePipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.piped.WindowPipeOperator.class);
        FORY.register(net.sf.jsqlparser.statement.refresh.RefreshMaterializedViewStatement.class);
        FORY.register(net.sf.jsqlparser.statement.refresh.RefreshMode.class);
        FORY.register(net.sf.jsqlparser.statement.select.AllColumns.class);
        FORY.register(net.sf.jsqlparser.statement.select.AllTableColumns.class);
        FORY.register(net.sf.jsqlparser.statement.select.Distinct.class);
        FORY.register(net.sf.jsqlparser.statement.select.ExceptOp.class);
        FORY.register(net.sf.jsqlparser.statement.select.Fetch.class);
        FORY.register(net.sf.jsqlparser.statement.select.First.class);
        FORY.register(net.sf.jsqlparser.statement.select.First.Keyword.class);
        FORY.register(net.sf.jsqlparser.statement.select.ForClause.class);
        FORY.register(net.sf.jsqlparser.statement.select.ForClause.ForOption.class);
        FORY.register(net.sf.jsqlparser.statement.select.ForMode.class);
        FORY.register(net.sf.jsqlparser.statement.select.FunctionAllColumns.class);
        FORY.register(net.sf.jsqlparser.statement.select.GroupByElement.class);
        FORY.register(net.sf.jsqlparser.statement.select.IntersectOp.class);
        FORY.register(net.sf.jsqlparser.statement.select.Join.class);
        FORY.register(net.sf.jsqlparser.statement.select.KSQLJoinWindow.class);
        FORY.register(net.sf.jsqlparser.statement.select.KSQLWindow.class);
        FORY.register(net.sf.jsqlparser.statement.select.KSQLWindow.TimeUnit.class);
        FORY.register(net.sf.jsqlparser.statement.select.KSQLWindow.WindowType.class);
        FORY.register(net.sf.jsqlparser.statement.select.LateralSubSelect.class);
        FORY.register(net.sf.jsqlparser.statement.select.LateralView.class);
        FORY.register(net.sf.jsqlparser.statement.select.Limit.class);
        FORY.register(net.sf.jsqlparser.statement.select.MinusOp.class);
        FORY.register(net.sf.jsqlparser.statement.select.MySqlSqlCacheFlags.class);
        FORY.register(net.sf.jsqlparser.statement.select.Offset.class);
        FORY.register(net.sf.jsqlparser.statement.select.OptimizeFor.class);
        FORY.register(net.sf.jsqlparser.statement.select.OrderByElement.class);
        FORY.register(net.sf.jsqlparser.statement.select.OrderByElement.NullOrdering.class);
        FORY.register(net.sf.jsqlparser.statement.select.ParenthesedFromItem.class);
        FORY.register(net.sf.jsqlparser.statement.select.ParenthesedSelect.class);
        FORY.register(net.sf.jsqlparser.statement.select.Pivot.class);
        FORY.register(net.sf.jsqlparser.statement.select.PivotXml.class);
        FORY.register(net.sf.jsqlparser.statement.select.PlainSelect.class);
        FORY.register(net.sf.jsqlparser.statement.select.PlainSelect.BigQuerySelectQualifier.class);
        FORY.register(net.sf.jsqlparser.statement.select.SampleClause.SampleKeyword.class);
        FORY.register(net.sf.jsqlparser.statement.select.SampleClause.SampleMethod.class);
        FORY.register(net.sf.jsqlparser.statement.select.Select.class);
        FORY.register(net.sf.jsqlparser.statement.select.SelectItem.class);
        FORY.register(net.sf.jsqlparser.statement.select.SetOperation.class);
        FORY.register(net.sf.jsqlparser.statement.select.SetOperationList.class);
        FORY.register(net.sf.jsqlparser.statement.select.SetOperationList.SetOperationType.class);
        FORY.register(net.sf.jsqlparser.statement.select.Skip.class);
        FORY.register(net.sf.jsqlparser.statement.select.TableFunction.class);
        FORY.register(net.sf.jsqlparser.statement.select.TableStatement.class);
        FORY.register(net.sf.jsqlparser.statement.select.Top.class);
        FORY.register(net.sf.jsqlparser.statement.select.UnPivot.class);
        FORY.register(net.sf.jsqlparser.statement.select.UnionOp.class);
        FORY.register(net.sf.jsqlparser.statement.select.Values.class);
        FORY.register(net.sf.jsqlparser.statement.select.Wait.class);
        FORY.register(net.sf.jsqlparser.statement.select.WithIsolation.class);
        FORY.register(net.sf.jsqlparser.statement.select.WithItem.class);
        FORY.register(net.sf.jsqlparser.statement.show.ShowIndexStatement.class);
        FORY.register(net.sf.jsqlparser.statement.show.ShowTablesStatement.class);
        FORY.register(net.sf.jsqlparser.statement.show.ShowTablesStatement.Modifiers.class);
        FORY.register(net.sf.jsqlparser.statement.show.ShowTablesStatement.SelectionMode.class);
        FORY.register(net.sf.jsqlparser.statement.truncate.Truncate.class);
        FORY.register(net.sf.jsqlparser.statement.update.ParenthesedUpdate.class);
        FORY.register(net.sf.jsqlparser.statement.update.Update.class);
        FORY.register(net.sf.jsqlparser.statement.update.UpdateModifierPriority.class);
        FORY.register(net.sf.jsqlparser.statement.update.UpdateSet.class);
        FORY.register(net.sf.jsqlparser.statement.upsert.Upsert.class);
        FORY.register(net.sf.jsqlparser.statement.upsert.UpsertType.class);
        FORY.register(net.sf.jsqlparser.util.cnfexpression.MultiAndExpression.class);
        FORY.register(net.sf.jsqlparser.util.cnfexpression.MultiOrExpression.class);
        FORY.register(net.sf.jsqlparser.util.cnfexpression.MultipleExpression.class);
        FORY.register(net.sf.jsqlparser.util.validation.allowedtypes.AllowedTypesContext.class);
        FORY.register(net.sf.jsqlparser.util.validation.feature.DatabaseType.class);
        FORY.register(net.sf.jsqlparser.util.validation.feature.FeatureContext.class);
        FORY.register(net.sf.jsqlparser.util.validation.feature.H2Version.class);
        FORY.register(net.sf.jsqlparser.util.validation.feature.MariaDbVersion.class);
        FORY.register(net.sf.jsqlparser.util.validation.feature.MySqlVersion.class);
        FORY.register(net.sf.jsqlparser.util.validation.feature.OracleVersion.class);
        FORY.register(net.sf.jsqlparser.util.validation.feature.PostgresqlVersion.class);
        FORY.register(net.sf.jsqlparser.util.validation.feature.SQLVersion.class);
        FORY.register(net.sf.jsqlparser.util.validation.feature.SqlServerVersion.class);
        FORY.register(net.sf.jsqlparser.util.validation.metadata.MetadataContext.class);
        FORY.register(net.sf.jsqlparser.util.validation.metadata.NamedObject.class);
        FORY.register(net.sf.jsqlparser.util.validation.metadata.NamesLookup.class);
    }

    public static ForyFactory getForyFactory() {
        return FACTORY;
    }

    public byte[] serialize(Object object) {
        if (object == null) {
            return new byte[0];
        }
        return FORY.serialize(object);
    }

    public Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return FORY.deserialize(bytes);
    }

}
